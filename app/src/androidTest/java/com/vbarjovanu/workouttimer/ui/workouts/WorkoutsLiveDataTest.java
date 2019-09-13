package com.vbarjovanu.workouttimer.ui.workouts;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.vbarjovanu.workouttimer.MainActivity;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
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

public class WorkoutsLiveDataTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void loadData() throws InterruptedException, IOException {
        IFileRepositorySettings fileRepoSettings;
        String filePath=null;
        String folderPath;
        final Observer<WorkoutsList> observer;
        String assetPath="business/services/workouts/Workouts-profile123.json";
        CountDownLatch countDownLatch;
        String profileId = "profile123";

        //export workouts-profile file from test assets to local file
        Context ctx = InstrumentationRegistry.getInstrumentation().getContext();
        try {
            File tempFile = folder.newFile("Workouts-profile123.json");
            filePath = tempFile.getPath();
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }
        AssetsFileExporter assetsFileExporter = new AssetsFileExporter(ctx);
        assetsFileExporter.exportAsset(assetPath, filePath);
        folderPath = filePath.replace("/Workouts-profile123.json", "");

        //init workoutslivedata with file repo settings
        fileRepoSettings = new FileRepositorySettings(folderPath);
        WorkoutsLiveData workoutsLiveData = new WorkoutsLiveData(fileRepoSettings);
        observer = mock(Observer.class);
        workoutsLiveData.observeForever(observer);
        //load data and check if observer's onChanged method was triggered
        countDownLatch = new CountDownLatch(1);
        workoutsLiveData.setCountDownLatch(countDownLatch);
        workoutsLiveData.loadWorkouts(profileId);
        countDownLatch.await();
        ArgumentCaptor<WorkoutsList> captor = ArgumentCaptor.forClass(WorkoutsList.class);
        Mockito.verify(observer, Mockito.times(1)).onChanged(captor.capture());
        //assert that expected workouts were loaded
        WorkoutsList workoutsList = captor.getValue();
        Assert.assertNotNull(workoutsList);
        Assert.assertEquals(2, workoutsList.size());
        Assert.assertEquals("123", workoutsList.get(0).getId());
        Assert.assertEquals("456", workoutsList.get(1).getId());
    }
}