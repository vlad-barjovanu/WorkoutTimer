package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.app.Application;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;
import com.vbarjovanu.workouttimer.ui.SingleLiveEvent;

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

public class UserProfileEditViewModelTest {

    private IUserProfileEditViewModel userProfileEditViewModel;
    private CountDownLatch countDownLatch;
    private Observer<UserProfile> observer;
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
        this.userProfileEditViewModel = new UserProfileEditViewModel((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext(), fileRepoSettings);
        //noinspection unchecked
        this.observer = mock(Observer.class);
        this.userProfileEditViewModel.getUserProfile().observeForever(this.observer);
        //load data and check if observer's onChanged method was triggered
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
    }

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void loadUserProfile() throws InterruptedException {
        String userProfileId = "abc";
        this.userProfileEditViewModel.loadUserProfile(userProfileId);
        countDownLatch.await();
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        //assert that expected workouts were loaded
        UserProfile userProfile = captor.getValue();
        Assert.assertNotNull(userProfile);
        Assert.assertEquals("abc", userProfile.getId());
    }

    @Test
    public void getUserProfile() {
        LiveData<UserProfile> userProfileLiveData = this.userProfileEditViewModel.getUserProfile();
        Assert.assertNotNull(userProfileLiveData);
        Assert.assertNull(userProfileLiveData.getValue());
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
        UserProfile userProfile = this.userProfileEditViewModel.getUserProfile().getValue();
        Assert.assertNotNull(userProfile);
        originalName = userProfile.getName();
        newName = originalName + " updated";
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
        oldDescription = userProfile.getDescription();
        this.userProfileEditViewModel.saveUserProfile(newName, oldDescription, null);
        this.countDownLatch.await();
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        Mockito.verify(this.observer, Mockito.times(2)).onChanged(captor.capture()); //2 time - one load, one save
        //assert that expected workouts were loaded
        userProfile = captor.getValue();
        Assert.assertNotNull(userProfile);
        Assert.assertEquals("abc", userProfile.getId());
        Assert.assertNotEquals(originalName, userProfile.getName());
        Assert.assertEquals(newName, userProfile.getName());
        Assert.assertEquals(oldDescription, userProfile.getDescription());
    }

    @Test
    public void cancelUserProfileEdit() throws InterruptedException {
        String userProfileId = "abc";

        this.userProfileEditViewModel.loadUserProfile(userProfileId);
        countDownLatch.await();
        UserProfile userProfile = this.userProfileEditViewModel.getUserProfile().getValue();
        Assert.assertNotNull(userProfile);
        this.countDownLatch = new CountDownLatch(1);
        this.userProfileEditViewModel.setCountDownLatch(countDownLatch);
        this.userProfileEditViewModel.cancelUserProfileEdit();
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        Mockito.verify(this.observer, Mockito.times(2)).onChanged(captor.capture()); //2 time - one load, one cancel
        //assert that expected workouts were loaded
        userProfile = captor.getValue();
        Assert.assertNull(userProfile);
    }
}