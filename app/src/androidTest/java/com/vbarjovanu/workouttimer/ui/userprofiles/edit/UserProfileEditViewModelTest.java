package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.IUserProfilesImagesService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

public class UserProfileEditViewModelTest {

    @Mock
    private IUserProfilesService userProfilesService;
    @Mock
    private IUserProfilesImagesService userProfilesImagesService;
    @Mock
    private IApplicationSession applicationSession;
    private IUserProfileEditViewModel userProfileEditViewModel;
    private CountDownLatch countDownLatch;
    private Observer<UserProfileModel> observer;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() throws IllegalAccessException, InvocationTargetException, InstantiationException {

        this.applicationSession = mock(IApplicationSession.class);
        this.userProfilesService = mock(IUserProfilesService.class);
        this.userProfilesImagesService = mock(IUserProfilesImagesService.class);

        //noinspection unchecked
        this.observer = mock(Observer.class);
        this.setupMockedAppSession();
        this.setupMockedUserProfiles();
        this.userProfileEditViewModel = new UserProfileEditViewModel(this.userProfilesService, userProfilesImagesService);
        this.userProfileEditViewModel.getUserProfileModel().observeForever(this.observer);
        //load data and check if observer's onChanged method was triggered
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
    }

    private void setupMockedAppSession() {
        Mockito.when(this.applicationSession.getFileRepositoriesFolderPath()).thenReturn(folder.getRoot().getAbsolutePath());
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn("123");
    }

    private void setupMockedUserProfiles() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        UserProfile userProfile;
        userProfile = new UserProfile("abc");
        Mockito.when(this.userProfilesService.loadModel("abc")).thenReturn(userProfile);
        userProfile = new UserProfile("def").setName("Default");
        Mockito.when(this.userProfilesService.createDefaultModel()).thenReturn(userProfile);
        userProfile = new UserProfile("ghi");
        Mockito.when(this.userProfilesService.createModel()).thenReturn(userProfile);
    }

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void loadUserProfile() throws InterruptedException {
        String userProfileId = "abc";
        this.userProfileEditViewModel.loadUserProfile(userProfileId);
        countDownLatch.await();
        ArgumentCaptor<UserProfileModel> captor = ArgumentCaptor.forClass(UserProfileModel.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        //assert that expected workouts were loaded
        UserProfileModel userProfileModel = captor.getValue();
        Assert.assertNotNull(userProfileModel);
        Assert.assertNotNull(userProfileModel.getUserProfile());
        Assert.assertEquals("abc", userProfileModel.getUserProfile().getId());
    }

    @Test
    public void newUserProfile() throws InterruptedException {
        this.userProfileEditViewModel.newUserProfile();
        countDownLatch.await();
        ArgumentCaptor<UserProfileModel> captor = ArgumentCaptor.forClass(UserProfileModel.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        //assert that expected workouts were loaded
        UserProfileModel userProfileModel = captor.getValue();
        Assert.assertNotNull(userProfileModel);
        Assert.assertNotNull(userProfileModel.getUserProfile());
        Assert.assertNotNull(userProfileModel.getUserProfile().getId());
    }

    @Test
    public void getUserProfileModel() {
        LiveData<UserProfileModel> userProfileModel = this.userProfileEditViewModel.getUserProfileModel();
        Assert.assertNotNull(userProfileModel);
        Assert.assertNull(userProfileModel.getValue());
    }

    @Test
    public void getAction() {
        SingleLiveEvent<UserProfileEditFragmentAction> action = this.userProfileEditViewModel.getAction();
        Assert.assertNotNull(action);
        Assert.assertNull(action.getValue());
    }

    @Test
    public void saveUserProfile() throws InterruptedException {
        String userProfileId = "abc";
        String originalName, newName;
        String oldDescription;

        this.userProfileEditViewModel.loadUserProfile(userProfileId);
        countDownLatch.await();
        UserProfileModel userProfileModel = this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNotNull(userProfileModel);
        originalName = userProfileModel.getUserProfile().getName();
        newName = originalName + " updated";
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
        oldDescription = userProfileModel.getUserProfile().getDescription();

        UserProfileModel userProfileModelToSave = new UserProfileModel(new UserProfile(userProfileModel.getUserProfile().getId()).setName(newName).setDescription(oldDescription), null);

        this.userProfileEditViewModel.saveUserProfile(userProfileModelToSave);
        this.countDownLatch.await();
        ArgumentCaptor<UserProfileModel> captor = ArgumentCaptor.forClass(UserProfileModel.class);
        Mockito.verify(this.observer, Mockito.times(2)).onChanged(captor.capture()); //2 time - one load, one save
        //assert that expected workouts were loaded
        userProfileModel = captor.getValue();
        Assert.assertNotNull(userProfileModel);
        Assert.assertEquals("abc", userProfileModel.getUserProfile().getId());
        Assert.assertNotEquals(originalName, userProfileModel.getUserProfile().getName());
        Assert.assertEquals(newName, userProfileModel.getUserProfile().getName());
        Assert.assertEquals(oldDescription, userProfileModel.getUserProfile().getDescription());
    }

    @Test
    public void cancelUserProfileEdit() throws InterruptedException {
        String userProfileId = "abc";

        this.userProfileEditViewModel.loadUserProfile(userProfileId);
        countDownLatch.await();
        UserProfileModel userProfileModel = this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNotNull(userProfileModel);
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
        this.userProfileEditViewModel.cancelUserProfileEdit();
        ArgumentCaptor<UserProfileModel> captor = ArgumentCaptor.forClass(UserProfileModel.class);
        Mockito.verify(this.observer, Mockito.times(2)).onChanged(captor.capture()); //2 time - one load, one cancel
        //assert that expected workouts were loaded
        userProfileModel = captor.getValue();
        Assert.assertNull(userProfileModel);
    }
}