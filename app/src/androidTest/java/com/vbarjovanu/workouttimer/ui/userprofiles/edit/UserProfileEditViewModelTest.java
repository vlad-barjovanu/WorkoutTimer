package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.graphics.Bitmap;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.preferences.IFileRepositoryPreferences;
import com.vbarjovanu.workouttimer.preferences.IWorkoutTimerPreferences;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
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
    private Observer<UserProfileModel> userProfileModelObserver;
    private Observer<UserProfileEditFragmentAction> actionObserver;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() throws IllegalAccessException, InvocationTargetException, InstantiationException {

        this.applicationSession = mock(IApplicationSession.class);
        this.userProfilesService = mock(IUserProfilesService.class);
        this.userProfilesImagesService = mock(IUserProfilesImagesService.class);
        this.userProfileEditViewModel = new UserProfileEditViewModel(this.userProfilesService, this.userProfilesImagesService);
        this.setupUserProfileModelObserver();
        this.setupActionObserver();
        this.setupMockedAppSession();
        this.setupMockedUserProfiles();
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
    }

    private void setupUserProfileModelObserver() {
        if (this.userProfileModelObserver != null) {
            this.userProfileEditViewModel.getUserProfileModel().removeObserver(this.userProfileModelObserver);
        }
        //noinspection unchecked
        this.userProfileModelObserver = mock(Observer.class);
        this.userProfileEditViewModel.getUserProfileModel().observeForever(this.userProfileModelObserver);
    }

    private void setupActionObserver() {
        if (this.actionObserver != null) {
            this.userProfileEditViewModel.getAction().removeObserver(this.actionObserver);
        }
        //noinspection unchecked
        this.actionObserver = mock(Observer.class);
        this.userProfileEditViewModel.getAction().observeForever(this.actionObserver);
    }

    private void setupMockedAppSession() {
        IWorkoutTimerPreferences workoutTimerPreferences = mock(IWorkoutTimerPreferences.class);
        IFileRepositoryPreferences fileRepositoryPreferences= mock(IFileRepositoryPreferences.class);
        Mockito.when(this.applicationSession.getWorkoutTimerPreferences()).thenReturn(workoutTimerPreferences);
        Mockito.when(workoutTimerPreferences.getFileRepositoryPreferences()).thenReturn(fileRepositoryPreferences);
        Mockito.when(fileRepositoryPreferences.getFolderPath()).thenReturn(folder.getRoot().getAbsolutePath());
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn("123");
    }

    private void setupMockedUserProfiles() throws IllegalAccessException, InstantiationException, InvocationTargetException {
        UserProfile userProfile;
        String userProfileId;
        userProfileId = "abc";
        userProfile = new UserProfile(userProfileId).setImageFilePath("//filepath");
        Mockito.when(this.userProfilesService.loadModel(userProfileId)).thenReturn(userProfile);
        userProfileId = "def";
        userProfile = new UserProfile(userProfileId).setName("Default");
        Mockito.when(this.userProfilesService.createDefaultModel()).thenReturn(userProfile);
        userProfileId = "ghi";
        userProfile = new UserProfile(userProfileId);
        Mockito.when(this.userProfilesService.createModel()).thenReturn(userProfile);
        Mockito.when(this.userProfilesImagesService.getUserImage(any(UserProfile.class))).thenAnswer((Answer<Bitmap>) invocation -> {
            UserProfile up = invocation.getArgument(0);
            if (up.getImageFilePath() != null && !up.getImageFilePath().isEmpty()) {
                return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
            }
            // default userImage is a 200x200
            return Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444);
        });
    }

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void loadUserProfile() throws InterruptedException {
        /*
         * loads an existing user profile by ID
         * expects: loading is done asynchronously
         * expects: user profile mode property of viewmodel is changed and returns the user profile with loaded ID
         * expects: no action change
         */
        String userProfileId = "abc";
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);
        this.userProfileEditViewModel.loadUserProfile(userProfileId, null);
        countDownLatch.await();
        //check that userProfileModel has changed and returns the loaded userProfile
        Mockito.verify(this.userProfileModelObserver, Mockito.times(1)).onChanged(userProfileModelCaptor.capture());
        UserProfileModel userProfileModel = userProfileModelCaptor.getValue();
        Assert.assertNotNull(userProfileModel);
        Assert.assertNotNull(userProfileModel.getUserProfile());
        Assert.assertEquals("abc", userProfileModel.getUserProfile().getId());
        //check that action has not changed
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void loadUserProfileNonExistingId() throws InterruptedException {
        /*
         * loads a non-existing user profile by ID
         * expects: loading is done asynchronously
         * expects: user profile model property of viewmodel has not changed and still returns null
         * expects: there's no action change
         */
        String userProfileId = "ab";//nonexistent ID
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);

        Assert.assertNull(this.userProfileEditViewModel.getUserProfileModel().getValue());
        this.userProfileEditViewModel.loadUserProfile(userProfileId, null);
        countDownLatch.await();
        //check that userProfileModel has changed and returns a null userProfile
        Mockito.verify(this.userProfileModelObserver, Mockito.times(0)).onChanged(userProfileModelCaptor.capture());
        Assert.assertNull(this.userProfileEditViewModel.getUserProfileModel().getValue());
        //check that action has not changed
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void newUserProfile() throws InterruptedException {
        /*
         * calls newUserProfile method
         * expects: viewmodel's userProfile property change
         * expects: no action change
         */
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);
        this.userProfileEditViewModel.newUserProfile(null);
        countDownLatch.await();
        //check that userProfileModel is changed and has the newly created userProfile
        Mockito.verify(this.userProfileModelObserver, Mockito.times(1)).onChanged(userProfileModelCaptor.capture());
        UserProfileModel userProfileModel = userProfileModelCaptor.getValue();
        Assert.assertNotNull(userProfileModel);
        Assert.assertNotNull(userProfileModel.getUserProfile());
        Assert.assertNotNull(userProfileModel.getUserProfile().getId());
        //check that action has not changed
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void getUserProfileModel() {
        /*
         * by default the LiveData of the UserProfileModel property is not null, but it doesn't have any actual content
         */
        LiveData<UserProfileModel> userProfileModel = this.userProfileEditViewModel.getUserProfileModel();
        Assert.assertNotNull(userProfileModel);
        Assert.assertNull(userProfileModel.getValue());
    }

    @Test
    public void getAction() {
        /*
         * by default the action event is not null, but it doesn't have any actual content
         */
        SingleLiveEvent<UserProfileEditFragmentAction> action = this.userProfileEditViewModel.getAction();
        Assert.assertNotNull(action);
        Assert.assertNull(action.getValue());
    }

    @Test
    public void saveUserProfile() throws InterruptedException {
        /*
         * calls saveUserProfile after having an userProfile loaded before
         * expects: save is done asynchronously
         * expects: userProfile is changed and returns the updated userProfile
         * expects: action is changed and has value GOTO_USER_PROFILES
         */
        String userProfileId = "abc";
        String originalName, newName;
        String oldDescription;
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);

        //load userProfile
        this.userProfileEditViewModel.loadUserProfile(userProfileId, null);
        countDownLatch.await();
        //modify userProfile
        UserProfileModel userProfileModel = this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNotNull(userProfileModel);
        originalName = userProfileModel.getUserProfile().getName();
        newName = originalName + " updated";
        oldDescription = userProfileModel.getUserProfile().getDescription();
        UserProfileModel userProfileModelToSave = new UserProfileModel(new UserProfile(userProfileModel.getUserProfile().getId()).setName(newName).setDescription(oldDescription), null);

        //save userProfile
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
        this.userProfileEditViewModel.saveUserProfile(userProfileModelToSave);
        this.countDownLatch.await();
        //check that userProfile is changed and has the updated values
        Mockito.verify(this.userProfileModelObserver, Mockito.times(2)).onChanged(userProfileModelCaptor.capture()); //2 time - one load, one save
        // check the changed userProfileModel
        userProfileModel = userProfileModelCaptor.getValue();
        Assert.assertNotNull(userProfileModel);
        Assert.assertEquals(userProfileId, userProfileModel.getUserProfile().getId());
        Assert.assertNotEquals(originalName, userProfileModel.getUserProfile().getName());
        Assert.assertEquals(newName, userProfileModel.getUserProfile().getName());
        Assert.assertEquals(oldDescription, userProfileModel.getUserProfile().getDescription());
        // check that action has changed and is GOTO_USERPROFILES
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfileEditFragmentAction action = actionCaptor.getValue();
        Assert.assertNotNull(action);
        Assert.assertEquals(UserProfileEditFragmentAction.GOTO_USERPROFILES, action);
    }

    @Test
    public void saveUserProfileWithoutLoading() throws InterruptedException {
        /*
         * calls saveUserProfile without having any userProfile loaded before
         * expects: save is done asynchronously
         * expects: userProfile is not changed and still returns null
         * expects: action is changed and has value GOTO_USER_PROFILES
         */
        String userProfileId = "abc";
        String originalName, newName;
        String oldDescription;
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);

        UserProfileModel userProfileModel = this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNull(userProfileModel);
        //modify userProfile
        userProfileModel = new UserProfileModel(new UserProfile(userProfileId).setName("Vlad").setDescription("My profile"), null);
        originalName = userProfileModel.getUserProfile().getName();
        newName = originalName + " updated";
        oldDescription = userProfileModel.getUserProfile().getDescription();
        UserProfileModel userProfileModelToSave = new UserProfileModel(new UserProfile(userProfileModel.getUserProfile().getId()).setName(newName).setDescription(oldDescription), null);

        //save userProfile
        this.userProfileEditViewModel.saveUserProfile(userProfileModelToSave);
        this.countDownLatch.await();
        //check that userProfile has changed and is null
        Mockito.verify(this.userProfileModelObserver, Mockito.times(0)).onChanged(userProfileModelCaptor.capture());
        UserProfileModel userProfileModelChanged =  this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNull(userProfileModelChanged);
        //check that action changed to GOTO_USER_PROFILES
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfileEditFragmentAction action = actionCaptor.getValue();
        Assert.assertEquals(UserProfileEditFragmentAction.GOTO_USERPROFILES, action);
    }

    @Test
    public void cancelUserProfileEdit() throws InterruptedException {
        /*
         * call cancelUserProfileEdit method after having an userProfile loaded before
         * expects: userProfile is changed and returns null
         * expects: action is changed and has value GOTO_USER_PROFILES
         */
        String userProfileId = "abc";
        ArgumentCaptor<UserProfileModel> captor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);

        //load userProfile
        this.userProfileEditViewModel.loadUserProfile(userProfileId, null);
        countDownLatch.await();
        UserProfileModel userProfileModel = this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNotNull(userProfileModel);
        //cancel userProfile edit
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
        this.userProfileEditViewModel.cancelUserProfileEdit();
        //check that userProfile has changed and is null
        Mockito.verify(this.userProfileModelObserver, Mockito.times(2)).onChanged(captor.capture()); //2 time - one load, one cancel
        userProfileModel = captor.getValue();
        Assert.assertNull(userProfileModel);
        //check that action has changed to GOTO_USERPROFILES
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfileEditFragmentAction action = actionCaptor.getValue();
        Assert.assertEquals(UserProfileEditFragmentAction.GOTO_USERPROFILES, action);
    }

    @Test
    public void cancelUserProfileEditWithoutLoading() {
        /*
         * call cancelUserProfileEdit method without having any userProfile loaded before
         * expects: userProfile is not changed and still returns null
         * expects: action is changed and has value GOTO_USER_PROFILES
         */
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);

        UserProfileModel userProfileModel = this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNull(userProfileModel);
        //cancel userProfile edit
        this.userProfileEditViewModel.cancelUserProfileEdit();
        Mockito.verify(this.userProfileModelObserver, Mockito.times(0)).onChanged(userProfileModelCaptor.capture());
        //check that userProfile has not changed and is still returning null
        userProfileModel = this.userProfileEditViewModel.getUserProfileModel().getValue();
        Assert.assertNull(userProfileModel);
        //check that action has changed to GOTO_USERPROFILES
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfileEditFragmentAction action = actionCaptor.getValue();
        Assert.assertEquals(UserProfileEditFragmentAction.GOTO_USERPROFILES, action);
    }

    @Test
    public void deleteUserImage() throws InterruptedException {
        /*
         * call deleteUserImage, after previously loading an userProfile
         * expects: userProfileModel is changed and has a default userImage
         * expects: userProfile is changed and has no imageFilePath
         * expects: action is not changed
         */
        String userProfileId = "abc";
        Bitmap userImage;
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);

        //user with ID "abc" has a image file path, so a userImage will be returned by the service
        this.userProfileEditViewModel.loadUserProfile(userProfileId, null);
        countDownLatch.await();
        Assert.assertNotNull(this.userProfileEditViewModel.getUserProfileModel().getValue());
        userImage = this.userProfileEditViewModel.getUserProfileModel().getValue().getUserImage();
        Assert.assertNotNull(userImage);
        //check that is the custom user image
        Assert.assertEquals(100, userImage.getWidth());
        Assert.assertEquals(100, userImage.getHeight());
        this.userProfileEditViewModel.deleteUserImage();
        Mockito.verify(this.userProfileModelObserver, Mockito.times(2)).onChanged(userProfileModelCaptor.capture());//1st time load, 2nd time deleteUserImage
        UserProfileModel userProfileModel = userProfileModelCaptor.getValue();
        Assert.assertNotNull(userProfileModel);
        userImage = userProfileModel.getUserImage();
        Assert.assertNotNull(userImage);
        //check that is the default user image
        Assert.assertEquals(200, userImage.getWidth());
        Assert.assertEquals(200, userImage.getHeight());
        Assert.assertNull(this.userProfileEditViewModel.getUserProfileModel().getValue().getUserProfile().getImageFilePath());
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void deleteUserImageWithoutLoading() {
        /*
         * call deleteUserImage without having any userProfile loaded before
         * expects: userProfileModel is not changed and still returns null
         * expects: action is not changed
         */
        ArgumentCaptor<UserProfileModel> userProfileModelCaptor = ArgumentCaptor.forClass(UserProfileModel.class);
        ArgumentCaptor<UserProfileEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(UserProfileEditFragmentAction.class);

        Assert.assertNull(this.userProfileEditViewModel.getUserProfileModel().getValue());
        this.userProfileEditViewModel.deleteUserImage();
        Mockito.verify(this.userProfileModelObserver, Mockito.times(0)).onChanged(userProfileModelCaptor.capture());
        Assert.assertNull(this.userProfileEditViewModel.getUserProfileModel().getValue());
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }
}