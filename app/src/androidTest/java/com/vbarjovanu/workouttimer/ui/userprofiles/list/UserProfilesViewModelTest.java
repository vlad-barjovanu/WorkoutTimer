package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesService;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.userprofiles.list.IUserProfilesViewModel;
import com.vbarjovanu.workouttimer.ui.userprofiles.list.UserProfilesViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

public class UserProfilesViewModelTest {
    @Mock
    private IUserProfilesService userProfilesService;
    @Mock
    private IApplicationSession applicationSession;
    private IUserProfilesViewModel userProfilesViewModel;
    private CountDownLatch countDownLatch;
    private Observer<UserProfilesList> observer;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() {
        this.applicationSession = mock(IApplicationSession.class);
        this.userProfilesService = mock(IUserProfilesService.class);
        //noinspection unchecked
        this.observer = mock(Observer.class);
        this.setupMockedAppSession();
        this.setupMockedUserProfiles();
        this.userProfilesViewModel = new UserProfilesViewModel(applicationSession, userProfilesService);
        this.userProfilesViewModel.getUserProfiles().observeForever(this.observer);
        //load data and check if observer's onChanged method was triggered
        this.countDownLatch = new CountDownLatch(1);
        userProfilesViewModel.setCountDownLatch(countDownLatch);
    }

    private void setupMockedAppSession() {
        Mockito.when(this.applicationSession.getFileRepositoriesFolderPath()).thenReturn(folder.getRoot().getAbsolutePath());
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn("123");
    }

    private void setupMockedUserProfiles() {
        UserProfilesList userProfilesList;
        userProfilesList = new UserProfilesList();
        userProfilesList.add(new UserProfile("abc"));
        userProfilesList.add(new UserProfile("def"));
        Mockito.when(this.userProfilesService.loadModels()).thenReturn(userProfilesList);
    }

    @Test
    public void loadUserProfiles() throws InterruptedException {
//        this.setupMockedUserProfiles();
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        ArgumentCaptor<UserProfilesList> captor = ArgumentCaptor.forClass(UserProfilesList.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        //assert that expected workouts were loaded
        UserProfilesList userProfilesList = captor.getValue();
        Assert.assertNotNull(userProfilesList);
        Assert.assertEquals(2, userProfilesList.size());
        Assert.assertEquals("abc", userProfilesList.get(0).getId());
        Assert.assertEquals("def", userProfilesList.get(1).getId());
    }

    @Test
    public void getWorkouts() {
        LiveData<UserProfilesList> userProfilesList = userProfilesViewModel.getUserProfiles();
        Assert.assertNotNull(userProfilesList);
    }

    @Test
    public void setSelectedWorkoutIdWhenNoWorkoutsAreLoaded() {
        Assert.assertFalse(userProfilesViewModel.setSelectedUserProfileId("abc"));
    }

    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoaded() throws InterruptedException {
//        this.setupMockedUserProfiles();
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Assert.assertTrue(userProfilesViewModel.setSelectedUserProfileId("abc"));
    }

    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoadedButWrongId() throws InterruptedException {
//        this.setupMockedUserProfiles();
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Assert.assertFalse(userProfilesViewModel.setSelectedUserProfileId("ab"));
    }

    @Test
    public void getSelectedWorkoutId() {
        Assert.assertNull(userProfilesViewModel.getSelectedUserProfileId());
    }

    @Test
    public void getSelectedWorkoutIdWhenWorkoutIsSelected() throws InterruptedException {
//        this.setupMockedUserProfiles();
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Assert.assertTrue(userProfilesViewModel.setSelectedUserProfileId("abc"));
        Assert.assertNotNull(userProfilesViewModel.getSelectedUserProfileId());
        Assert.assertEquals("abc", userProfilesViewModel.getSelectedUserProfileId());
    }
}