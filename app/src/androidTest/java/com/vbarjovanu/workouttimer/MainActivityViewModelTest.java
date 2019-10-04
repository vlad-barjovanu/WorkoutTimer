package com.vbarjovanu.workouttimer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesFactory;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MainActivityViewModelTest {

    private IMainActivityViewModel mainActivityViewModel;
    private Observer<EventContent<MainActivityActionData>> observer;
    private CountDownLatch countDownLatch;
    private Context context;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    @Mock
    private IUserProfilesService userProfilesService;
    @Mock
    private IApplicationSession applicationSession;

    @Before
    public void setup() {
        this.userProfilesService = mock(IUserProfilesService.class);
        this.applicationSession = mock(IApplicationSession.class);

        context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        // clear all prev stored preferences, so unit tests are run clean
        sharedPreferences.edit().clear().commit();
        mainActivityViewModel = new MainActivityViewModel(this.applicationSession, this.userProfilesService);
        //noinspection unchecked
        this.observer = mock(Observer.class);
        mainActivityViewModel.getAction().observeForever(this.observer);
        this.countDownLatch = new CountDownLatch(1);
        mainActivityViewModel.getAction().setCountDownLatch(countDownLatch);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitUserProfileNoUserProfileAndNoneAvailable() throws InterruptedException, IllegalAccessException, InstantiationException, InvocationTargetException {
        /*
         * When no user profile is selected and no user profiles are available,
         * a default user profile will be created and selected
         */
        UserProfilesList userProfilesList = new UserProfilesList();
        UserProfile userProfileDefault = new UserProfile("123-default");
        userProfileDefault.setName("Default");
        Mockito.when(this.userProfilesService.loadModels()).thenReturn(userProfilesList);
        Mockito.when(this.userProfilesService.createDefaultModel()).thenReturn(userProfileDefault);
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn(null);
        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<EventContent<MainActivityActionData>> captor = ArgumentCaptor.forClass(EventContent.class);
        verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        EventContent<MainActivityActionData> eventContent = captor.getValue();
        Assert.assertNotNull(eventContent);
        MainActivityActionData mainActivityActionData = eventContent.getContent();
        Assert.assertNotNull(mainActivityActionData);
        MainActivityAction mainActivityAction = mainActivityActionData.getAction();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_HOME, mainActivityAction);
        //the newly created default profile must be set in app session
        verify(this.applicationSession, times(1)).setUserProfileId("123-default");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitUserProfileInvalidUserProfileSelected() throws InterruptedException, IllegalAccessException, InstantiationException, InvocationTargetException {
        /*
         * When no user profiles are available and a non-valid user profile ID is selected,
         * a default user profile will be created and selected
         */
        UserProfilesList userProfilesList = new UserProfilesList();
        UserProfile userProfileDefault = new UserProfile("123-default");
        userProfileDefault.setName("Default");
        Mockito.when(this.userProfilesService.loadModels()).thenReturn(userProfilesList);
        Mockito.when(this.userProfilesService.createDefaultModel()).thenReturn(userProfileDefault);
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn("123");
        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<EventContent<MainActivityActionData>> captor = ArgumentCaptor.forClass(EventContent.class);
        verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        EventContent<MainActivityActionData> eventContent = captor.getValue();
        Assert.assertNotNull(eventContent);
        MainActivityActionData mainActivityActionData = eventContent.getContent();
        Assert.assertNotNull(mainActivityActionData);
        MainActivityAction mainActivityAction = mainActivityActionData.getAction();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_HOME, mainActivityAction);
        //the newly created default profile must be set in app session
        verify(this.applicationSession, times(1)).setUserProfileId("123-default");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitUserProfileValidUserProfileSelected() throws InterruptedException, IllegalAccessException, InstantiationException, InvocationTargetException {
        /*
         * When user profiles are available and a valid user profile is already selected,
         * the app should redirect to the home screen and the selected user profile should remain unchanged
         */
        UserProfilesList userProfilesList = new UserProfilesList();
        userProfilesList.add(new UserProfile("abc"));
        userProfilesList.add(new UserProfile("def"));
        Mockito.when(this.userProfilesService.loadModels()).thenReturn(userProfilesList);
        Mockito.when(this.userProfilesService.loadModel("abc")).thenReturn(userProfilesList.find("abc"));
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn("abc");

        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<EventContent<MainActivityActionData>> captor = ArgumentCaptor.forClass(EventContent.class);
        verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        EventContent<MainActivityActionData> eventContent = captor.getValue();
        Assert.assertNotNull(eventContent);
        MainActivityActionData mainActivityActionData = eventContent.getContent();
        Assert.assertNotNull(mainActivityActionData);
        MainActivityAction mainActivityAction = mainActivityActionData.getAction();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_HOME, mainActivityAction);
        verify(this.userProfilesService, times(0)).createDefaultModel();
        verify(this.applicationSession, times(0)).setUserProfileId("abc");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitUserProfileNoUserProfileSelectedAndMoreThanOneAvailable() throws InterruptedException, IllegalAccessException, InstantiationException, InvocationTargetException {
        /*
         * When no user profile is selected and more user profiles are available,
         * the app should redirect to the user profiles fragment and no user profile should be selected
         */
        UserProfilesList userProfilesList = new UserProfilesList();
        userProfilesList.add(new UserProfile("abc"));
        userProfilesList.add(new UserProfile("def"));
        Mockito.when(this.userProfilesService.loadModels()).thenReturn(userProfilesList);
        Mockito.when(this.userProfilesService.loadModel("abc")).thenReturn(userProfilesList.find("abc"));
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn(null);

        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<EventContent<MainActivityActionData>> captor = ArgumentCaptor.forClass(EventContent.class);
        verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        EventContent<MainActivityActionData> eventContent = captor.getValue();
        Assert.assertNotNull(eventContent);
        MainActivityActionData mainActivityActionData = eventContent.getContent();
        Assert.assertNotNull(mainActivityActionData);
        MainActivityAction mainActivityAction = mainActivityActionData.getAction();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_USERPROFILES, mainActivityAction);
        verify(this.userProfilesService, times(0)).createDefaultModel();
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(this.applicationSession, times(0)).setUserProfileId(argument.capture());
    }
}