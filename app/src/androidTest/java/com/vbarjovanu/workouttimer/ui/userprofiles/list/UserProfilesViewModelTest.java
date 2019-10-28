package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.preferences.IFileRepositoryPreferences;
import com.vbarjovanu.workouttimer.preferences.IWorkoutTimerPreferences;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

public class UserProfilesViewModelTest {
    @Mock
    private IUserProfilesService userProfilesService;
    @Mock
    private IApplicationSession applicationSession;
    private IUserProfilesViewModel userProfilesViewModel;
    private CountDownLatch countDownLatch;
    private Observer<UserProfilesList> userProfilesObserver;
    private Observer<UserProfilesFragmentActionData> actionObserver;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() {
        this.applicationSession = mock(IApplicationSession.class);
        this.userProfilesService = mock(IUserProfilesService.class);
        this.userProfilesViewModel = new UserProfilesViewModel(applicationSession, userProfilesService);
        this.setupUserProfilesObserver();
        this.setupActionObserver();
        this.setupMockedAppSession();
        this.setupMockedUserProfiles();
        this.countDownLatch = new CountDownLatch(1);
        userProfilesViewModel.setCountDownLatch(countDownLatch);
    }

    private void setupUserProfilesObserver() {
        if (this.userProfilesObserver != null) {
            this.userProfilesViewModel.getUserProfiles().removeObserver(this.userProfilesObserver);
        }
        //noinspection unchecked
        this.userProfilesObserver = mock(Observer.class);
        this.userProfilesViewModel.getUserProfiles().observeForever(this.userProfilesObserver);
    }

    private void setupActionObserver() {
        if (this.actionObserver != null) {
            this.userProfilesViewModel.getActionData().removeObserver(this.actionObserver);
        }
        //noinspection unchecked
        this.actionObserver = mock(Observer.class);
        this.userProfilesViewModel.getActionData().observeForever(this.actionObserver);
    }

    private void setupMockedAppSession() {
        IWorkoutTimerPreferences workoutTimerPreferences = mock(IWorkoutTimerPreferences.class);
        IFileRepositoryPreferences fileRepositoryPreferences= mock(IFileRepositoryPreferences.class);
        Mockito.when(this.applicationSession.getWorkoutTimerPreferences()).thenReturn(workoutTimerPreferences);
        Mockito.when(workoutTimerPreferences.getFileRepositoryPreferences()).thenReturn(fileRepositoryPreferences);
        Mockito.when(fileRepositoryPreferences.getFolderPath()).thenReturn(folder.getRoot().getAbsolutePath());
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
        /*
         * call loadUserProfiles()
         * expect: UserProfilesList change
         * expect: UserProfilesList with 2 records with user profiles IDs "abc", "def"
         * expect: no action data change
         */
        ArgumentCaptor<UserProfilesList> userProfilesListCaptor = ArgumentCaptor.forClass(UserProfilesList.class);
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Mockito.verify(this.userProfilesObserver, Mockito.times(1)).onChanged(userProfilesListCaptor.capture());
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
        //assert that expected workouts were loaded
        UserProfilesList userProfilesList = userProfilesListCaptor.getValue();
        Assert.assertNotNull(userProfilesList);
        Assert.assertEquals(2, userProfilesList.size());
        Assert.assertEquals("abc", userProfilesList.get(0).getId());
        Assert.assertEquals("def", userProfilesList.get(1).getId());
    }

    @Test
    public void getUserProfiles() {
        /*
         * by default getUserProfiles LiveData object is not null but it has no actual content
         */
        LiveData<UserProfilesList> userProfilesList = userProfilesViewModel.getUserProfiles();
        Assert.assertNotNull(userProfilesList);
        Assert.assertNull(userProfilesList.getValue());
    }

    @Test
    public void setSelectedWorkoutIdWhenNoWorkoutsAreLoaded() {
        /*
         * select a user profile ID, when no user profiles are loaded
         * expect: method will return false and no action data will not change
         */
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        Assert.assertFalse(userProfilesViewModel.setSelectedUserProfileId("abc"));
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoaded() throws InterruptedException {
        /*
         * select a user profile ID that exists, after user profiles were loaded
         * expect: method will return true and action data will change with action GOTO_HOME and with the selected user profile ID
         */
        String userProfileId = "abc";
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        Assert.assertTrue(userProfilesViewModel.setSelectedUserProfileId(userProfileId));
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfilesFragmentActionData actionData = actionCaptor.getValue();
        Assert.assertEquals(userProfileId, actionData.getUserProfileId());
        Assert.assertEquals(UserProfilesFragmentAction.GOTO_HOME, actionData.getAction());
    }

    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoadedButWrongId() throws InterruptedException {
        /*
         * select a user profile ID that doesn't exist, after user profiles were loaded
         * expect: method will return false and action data will not change
         */
        String userProfileId = "ab";
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        Assert.assertFalse(userProfilesViewModel.setSelectedUserProfileId(userProfileId));
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void getSelectedWorkoutId() {
        /*
         * by default there's no selected user profile ID
         */
        Assert.assertNull(userProfilesViewModel.getSelectedUserProfileId());
    }

    @Test
    public void getSelectedWorkoutIdWhenWorkoutIsSelected() throws InterruptedException {
        /*
         * load user profiles and select an existing user profile ID
         * call method and expect to return the previously selected user profile ID
         */
        String userProfileId = "abc";
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Assert.assertTrue(userProfilesViewModel.setSelectedUserProfileId(userProfileId));
        Assert.assertNotNull(userProfilesViewModel.getSelectedUserProfileId());
        Assert.assertEquals(userProfileId, userProfilesViewModel.getSelectedUserProfileId());
    }

    @Test
    public void getActionData() {
        /*
         * by default action data event is not null, but it contains no actual data
         */
        SingleLiveEvent<UserProfilesFragmentActionData> actionData = this.userProfilesViewModel.getActionData();
        Assert.assertNotNull(actionData);
        Assert.assertNull(actionData.getValue());
    }

    @Test
    public void editUserProfileNoUserProfilesLoaded() {
        /*
         * no user profiles are loaded and the editUserProfile is called
         * expect: method returns false
         * expect: no actionData changes
         */
        String userProfileId = "abc";
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        Assert.assertFalse(this.userProfilesViewModel.editUserProfile(userProfileId));
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void editUserProfileWrongId() throws InterruptedException {
        /*
         * user profiles are loaded but the editUserProfile is called with a non-existing ID
         * expect: method returns false
         * expect: no actionData changes
         * expect: userProfilesList doesn't change again after load (only 1 time in total)
         */
        String userProfileId = "ab";
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        ArgumentCaptor<UserProfilesList> userProfilesListCaptor = ArgumentCaptor.forClass(UserProfilesList.class);
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Mockito.verify(this.userProfilesObserver, Mockito.times(1)).onChanged(userProfilesListCaptor.capture());
        Assert.assertFalse(this.userProfilesViewModel.editUserProfile(userProfileId));
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
        Mockito.verify(this.userProfilesObserver, Mockito.times(1)).onChanged(userProfilesListCaptor.capture());
    }

    @Test
    public void editUserProfile() throws InterruptedException {
        /*
         * user profiles are loaded and the editUserProfile is called with an existing ID
         * expect: method returns true
         * expect: actionData changes with GOTO_USERPROFILE_EDIT and with edited user profile ID
         * expect: userProfilesList doesn't change again after load (only 1 time in total)
         */
        String userProfileId = "abc";
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        ArgumentCaptor<UserProfilesList> userProfilesListCaptor = ArgumentCaptor.forClass(UserProfilesList.class);
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Mockito.verify(this.userProfilesObserver, Mockito.times(1)).onChanged(userProfilesListCaptor.capture());
        Assert.assertTrue(this.userProfilesViewModel.editUserProfile(userProfileId));
        Mockito.verify(this.userProfilesObserver, Mockito.times(1)).onChanged(userProfilesListCaptor.capture());
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfilesFragmentActionData actionData = actionCaptor.getValue();
        Assert.assertNotNull(actionData);
        Assert.assertEquals(UserProfilesFragmentAction.GOTO_USERPROFILE_EDIT, actionData.getAction());
        Assert.assertEquals(userProfileId, actionData.getUserProfileId());
    }
    @Test
    public void newUserProfileNoUserProfilesLoaded() {
        /*
         * no user profiles are loaded and the newUserProfile is called
         * expect: method returns true
         * expect: actionData changes with GOTO_USERPROFILE_NEW
         * expect: userProfiles list doesn't change
         */
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        ArgumentCaptor<UserProfilesList> userProfilesListCaptor = ArgumentCaptor.forClass(UserProfilesList.class);
        Assert.assertTrue(this.userProfilesViewModel.newUserProfile());
        Mockito.verify(this.userProfilesObserver, Mockito.times(0)).onChanged(userProfilesListCaptor.capture());
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfilesFragmentActionData actionData = actionCaptor.getValue();
        Assert.assertNotNull(actionData);
        Assert.assertEquals(UserProfilesFragmentAction.GOTO_USERPROFILE_NEW, actionData.getAction());
        Assert.assertNull(actionData.getUserProfileId());
    }

    @Test
    public void newUserProfile() throws InterruptedException {
        /*
         * user profiles are loaded and the newUserProfile is called
         * expect: method returns true
         * expect: actionData changes with GOTO_USERPROFILE_NEW
         * expect: userProfiles list doesn't change
         */
        ArgumentCaptor<UserProfilesFragmentActionData> actionCaptor = ArgumentCaptor.forClass(UserProfilesFragmentActionData.class);
        ArgumentCaptor<UserProfilesList> userProfilesListCaptor = ArgumentCaptor.forClass(UserProfilesList.class);
        this.userProfilesViewModel.loadUserProfiles();
        countDownLatch.await();
        Mockito.verify(this.userProfilesObserver, Mockito.times(1)).onChanged(userProfilesListCaptor.capture());
        Assert.assertTrue(this.userProfilesViewModel.newUserProfile());
        Mockito.verify(this.userProfilesObserver, Mockito.times(1)).onChanged(userProfilesListCaptor.capture());
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(actionCaptor.capture());
        UserProfilesFragmentActionData actionData = actionCaptor.getValue();
        Assert.assertNotNull(actionData);
        Assert.assertEquals(UserProfilesFragmentAction.GOTO_USERPROFILE_NEW, actionData.getAction());
        Assert.assertNull(actionData.getUserProfileId());
    }}