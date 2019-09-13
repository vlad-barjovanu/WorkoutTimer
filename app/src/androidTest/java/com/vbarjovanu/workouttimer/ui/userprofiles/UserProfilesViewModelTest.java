package com.vbarjovanu.workouttimer.ui.userprofiles;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;
import com.vbarjovanu.workouttimer.ui.workouts.WorkoutsLiveData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

public class UserProfilesViewModelTest {
    private IUserProfilesViewModel userProfilesViewModel;
    private CountDownLatch countDownLatch;
    private Observer<UserProfilesList> observer;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() throws IOException {
        IFileRepositorySettings fileRepoSettings;
        String filePath = null;
        String folderPath;
        String assetPath = "business/services/userprofiles/UserProfiles.json";

        //export workouts-profile file from test assets to local file
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        try {
            File tempFile = folder.newFile("UserProfiles.json");
            filePath = tempFile.getPath();
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }
        AssetsFileExporter assetsFileExporter = new AssetsFileExporter(ctx);
        assetsFileExporter.exportAsset(assetPath, filePath);
        folderPath = filePath.replace("/UserProfiles.json", "");

        //init workoutslivedata with file repo settings
        fileRepoSettings = new FileRepositorySettings(folderPath);
        this.userProfilesViewModel = new UserProfilesViewModel(fileRepoSettings);
        this.observer = mock(Observer.class);
        this.userProfilesViewModel.getUserProfiles().observeForever(this.observer);
        //load data and check if observer's onChanged method was triggered
        this.countDownLatch = new CountDownLatch(1);
        userProfilesViewModel.getUserProfiles().setCountDownLatch(countDownLatch);
    }

    @Test
    public void loadUserProfiles() throws InterruptedException {
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
        UserProfilesLiveData workoutsLiveData = userProfilesViewModel.getUserProfiles();
        Assert.assertNotNull(workoutsLiveData);
    }

    @Test
    public void setSelectedWorkoutIdWhenNoWorkoutsAreLoaded() {
        Assert.assertFalse(userProfilesViewModel.setSelectedUserProfileId("abc"));
    }
    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoaded() throws InterruptedException {
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Assert.assertTrue(userProfilesViewModel.setSelectedUserProfileId("abc"));
    }
    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoadedButWrongId() throws InterruptedException {
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
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Assert.assertTrue(userProfilesViewModel.setSelectedUserProfileId("abc"));
        Assert.assertNotNull(userProfilesViewModel.getSelectedUserProfileId());
        Assert.assertEquals("abc", userProfilesViewModel.getSelectedUserProfileId());
    }
}