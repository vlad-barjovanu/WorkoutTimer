package com.vbarjovanu.workouttimer.ui.workouts;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;

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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class WorkoutsViewModelTest {

    private WorkoutsViewModel workoutsViewModel;
    private CountDownLatch countDownLatch;
    private Observer<WorkoutsList> observer;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() throws IOException {
        IFileRepositorySettings fileRepoSettings;
        String filePath = null;
        String folderPath;
        String assetPath = "business/services/workouts/Workouts-profile123.json";

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
        this.workoutsViewModel = new WorkoutsViewModel(fileRepoSettings);
        this.observer = mock(Observer.class);
        this.workoutsViewModel.getWorkouts().observeForever(this.observer);
        //load data and check if observer's onChanged method was triggered
        this.countDownLatch = new CountDownLatch(1);
        workoutsViewModel.getWorkouts().setCountDownLatch(countDownLatch);
    }

    @Test
    public void loadWorkouts() throws InterruptedException {
        String profileId = "profile123";

        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        ArgumentCaptor<WorkoutsList> captor = ArgumentCaptor.forClass(WorkoutsList.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        //assert that expected workouts were loaded
        WorkoutsList workoutsList = captor.getValue();
        Assert.assertNotNull(workoutsList);
        Assert.assertEquals(2, workoutsList.size());
        Assert.assertEquals("123", workoutsList.get(0).getId());
        Assert.assertEquals("456", workoutsList.get(1).getId());
    }

    @Test
    public void getWorkouts() {
        WorkoutsLiveData workoutsLiveData = workoutsViewModel.getWorkouts();
        Assert.assertNotNull(workoutsLiveData);
    }

    @Test
    public void setSelectedWorkoutIdWhenNoWorkoutsAreLoaded() {
        Assert.assertFalse(workoutsViewModel.setSelectedWorkoutId("123"));
    }
    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoaded() throws InterruptedException {
        String profileId = "profile123";
        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        Assert.assertTrue(workoutsViewModel.setSelectedWorkoutId("123"));
    }
    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoadedButWrongId() throws InterruptedException {
        String profileId = "profile123";
        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        Assert.assertFalse(workoutsViewModel.setSelectedWorkoutId("12"));
    }

    @Test
    public void getSelectedWorkoutId() {
        Assert.assertNull(workoutsViewModel.getSelectedWorkoutId());
    }

    @Test
    public void getSelectedWorkoutIdWhenWorkoutIsSelected() throws InterruptedException {
        String profileId = "profile123";
        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        Assert.assertTrue(workoutsViewModel.setSelectedWorkoutId("123"));
        Assert.assertNotNull(workoutsViewModel.getSelectedWorkoutId());
        Assert.assertEquals("123", workoutsViewModel.getSelectedWorkoutId());
    }
}