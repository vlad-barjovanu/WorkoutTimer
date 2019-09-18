package com.vbarjovanu.workouttimer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.business.services.userprofiles.UserProfilesFactory;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

public class MainActivityViewModelTest {

    private IMainActivityViewModel mainActivityViewModel;
    private Observer<MainActivityAction> observer;
    private CountDownLatch countDownLatch;
    private Context context;
    private FileRepositorySettings fileRepoSettings;
    private File fileRepoFolder;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        InstrumentationRegistry.getInstrumentation();
        fileRepoFolder = context.getDir("filerepository", Context.MODE_PRIVATE);
        if (fileRepoFolder.exists()) {
            //clear existing repository to start tests clean
            for (String filePath : fileRepoFolder.list()) {
                File currentFile = new File(fileRepoFolder.getPath(), filePath);
                currentFile.delete();
            }
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        // clear all prev stored preferences, so unit tests are run clean
        sharedPreferences.edit().clear().commit();
        String folderPath = fileRepoFolder.getPath();
        fileRepoSettings = new FileRepositorySettings(folderPath);
        mainActivityViewModel = new MainActivityViewModel((Application) context, fileRepoSettings);
        //noinspection unchecked
        this.observer = mock(Observer.class);
        mainActivityViewModel.getAction().observeForever(this.observer);
        this.countDownLatch = new CountDownLatch(1);
        mainActivityViewModel.getAction().setCountDownLatch(countDownLatch);
    }

    @Test
    public void testInitUserProfileNoUserProfileAndNoneAvailable() throws InterruptedException {
        /*
         * When no user profile is selected and no user profiles are available,
         * a default user profile will be created and selected
         */
        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<MainActivityAction> captor = ArgumentCaptor.forClass(MainActivityAction.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        MainActivityAction mainActivityAction = captor.getValue();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_HOME, mainActivityAction);
        String userProfileId = ApplicationSessionFactory.getApplicationSession(this.context).getUserProfileId();
        Assert.assertNotNull(userProfileId);
        IUserProfilesService userProfilesService = UserProfilesFactory.getUserProfilesService(this.fileRepoSettings);
        UserProfile userProfile = userProfilesService.loadModel(userProfileId);
        Assert.assertNotNull(userProfile);
        Assert.assertEquals("Default", userProfile.getName());
    }

    @Test
    public void testInitUserProfileInvalidUserProfileSelected() throws InterruptedException {
        /*
         * When no user profiles are available and a non-valid user profile ID is selected,
         * a default user profile will be created and selected
         */
        ApplicationSessionFactory.getApplicationSession(this.context).setUserProfileId("123");
        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<MainActivityAction> captor = ArgumentCaptor.forClass(MainActivityAction.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        MainActivityAction mainActivityAction = captor.getValue();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_HOME, mainActivityAction);
        String userProfileId = ApplicationSessionFactory.getApplicationSession(this.context).getUserProfileId();
        Assert.assertNotNull(userProfileId);
        IUserProfilesService userProfilesService = UserProfilesFactory.getUserProfilesService(this.fileRepoSettings);
        UserProfile userProfile = userProfilesService.loadModel(userProfileId);
        Assert.assertNotNull(userProfile);
        Assert.assertNotEquals("123", userProfile.getId());
        Assert.assertEquals("Default", userProfile.getName());
    }

    @Test
    public void testInitUserProfileValidUserProfileSelected() throws InterruptedException, IOException {
        /*
         * When user profiles are available and a valid user profile is already selected,
         * the app should redirect to the home screen and the selected user profile should remain unchanged
         */
        String assetPath = "business/services/userprofiles/UserProfiles.json";
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        AssetsFileExporter assetsFileExporter = new AssetsFileExporter(ctx);
        String filePath = fileRepoFolder.getPath() + "/UserProfiles.json";
        assetsFileExporter.exportAsset(assetPath, filePath);

        ApplicationSessionFactory.getApplicationSession(this.context).setUserProfileId("abc");
        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<MainActivityAction> captor = ArgumentCaptor.forClass(MainActivityAction.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        MainActivityAction mainActivityAction = captor.getValue();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_HOME, mainActivityAction);
        Assert.assertNotNull(ApplicationSessionFactory.getApplicationSession(this.context).getUserProfileId());
        Assert.assertEquals("abc", ApplicationSessionFactory.getApplicationSession(this.context).getUserProfileId());
    }

    @Test
    public void testInitUserProfileNoUserProfileSelectedAndMoreThanOneAvailable() throws InterruptedException, IOException {
        /*
         * When no user profile is selected and more user profiles are available,
         * the app should redirect to the user profiles fragment and no user profile should be selected
         */
        String assetPath = "business/services/userprofiles/UserProfiles.json";
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        AssetsFileExporter assetsFileExporter = new AssetsFileExporter(ctx);
        String filePath = fileRepoFolder.getPath() + "/UserProfiles.json";
        assetsFileExporter.exportAsset(assetPath, filePath);

        mainActivityViewModel.initUserProfile();
        this.countDownLatch.await();
        ArgumentCaptor<MainActivityAction> captor = ArgumentCaptor.forClass(MainActivityAction.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        MainActivityAction mainActivityAction = captor.getValue();
        Assert.assertNotNull(mainActivityAction);
        Assert.assertEquals(MainActivityAction.GOTO_USERPROFILES, mainActivityAction);
        Assert.assertNull(ApplicationSessionFactory.getApplicationSession(this.context).getUserProfileId());
    }
}