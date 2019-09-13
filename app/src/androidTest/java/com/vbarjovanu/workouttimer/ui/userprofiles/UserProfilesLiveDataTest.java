package com.vbarjovanu.workouttimer.ui.userprofiles;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

public class UserProfilesLiveDataTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void loadData() throws InterruptedException, IOException {
        IFileRepositorySettings fileRepoSettings;
        String filePath=null;
        String folderPath;
        final Observer<UserProfilesList> observer;
        String assetPath="business/services/userprofiles/UserProfiles.json";
        CountDownLatch countDownLatch;

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
        UserProfilesLiveData userProfilesLiveData = new UserProfilesLiveData(fileRepoSettings);
        observer = mock(Observer.class);
        userProfilesLiveData.observeForever(observer);
        //load data and check if observer's onChanged method was triggered
        countDownLatch = new CountDownLatch(1);
        userProfilesLiveData.setCountDownLatch(countDownLatch);
        userProfilesLiveData.loadUserProfiles();
        countDownLatch.await();
        ArgumentCaptor<UserProfilesList> captor = ArgumentCaptor.forClass(UserProfilesList.class);
        Mockito.verify(observer, Mockito.times(1)).onChanged(captor.capture());
        //assert that expected workouts were loaded
        UserProfilesList workoutsList = captor.getValue();
        Assert.assertNotNull(workoutsList);
        Assert.assertEquals(2, workoutsList.size());
        Assert.assertEquals("abc", workoutsList.get(0).getId());
        Assert.assertEquals("def", workoutsList.get(1).getId());
    }
}