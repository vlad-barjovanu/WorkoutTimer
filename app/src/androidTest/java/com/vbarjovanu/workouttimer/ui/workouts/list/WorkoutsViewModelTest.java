package com.vbarjovanu.workouttimer.ui.workouts.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.mockito.Mockito.mock;

public class WorkoutsViewModelTest {

    @Mock
    private IWorkoutsService workoutsService;
    private IWorkoutsViewModel workoutsViewModel;
    private CountDownLatch countDownLatch;
    private Observer<WorkoutsList> observer;

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() {
        this.workoutsService = mock(IWorkoutsService.class);
        //noinspection unchecked
        this.observer = mock(Observer.class);
        this.workoutsViewModel = new WorkoutsViewModel(this.workoutsService);
        this.workoutsViewModel.getWorkouts().observeForever(this.observer);
        //load data and check if observer's onChanged method was triggered
        this.countDownLatch = new CountDownLatch(1);
        workoutsViewModel.setCountDownLatch(countDownLatch);
    }

    private void setupMockedWorkouts(String profileId){
        WorkoutsList workoutsListMocked;
        workoutsListMocked = new WorkoutsList();
        workoutsListMocked.add(new Workout("123"));
        workoutsListMocked.add(new Workout("456"));
        Mockito.when(this.workoutsService.loadModels(profileId)).thenReturn(workoutsListMocked);
    }

    @Test
    public void loadWorkouts() throws InterruptedException {
        String profileId = "profile123";
        this.setupMockedWorkouts(profileId);
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
        LiveData<WorkoutsList> workoutsLiveData = workoutsViewModel.getWorkouts();
        Assert.assertNotNull(workoutsLiveData);
    }

    @Test
    public void setSelectedWorkoutIdWhenNoWorkoutsAreLoaded() {
        Assert.assertFalse(workoutsViewModel.setSelectedWorkoutId("123"));
    }

    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoaded() throws InterruptedException {
        String profileId = "profile123";
        this.setupMockedWorkouts(profileId);
        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        Assert.assertTrue(workoutsViewModel.setSelectedWorkoutId("123"));
    }

    @Test
    public void setSelectedWorkoutIdWhenWorkoutsAreLoadedButWrongId() throws InterruptedException {
        String profileId = "profile123";
        this.setupMockedWorkouts(profileId);
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
        this.setupMockedWorkouts(profileId);
        this.workoutsViewModel.loadWorkouts(profileId);
        countDownLatch.await();
        Assert.assertTrue(workoutsViewModel.setSelectedWorkoutId("123"));
        Assert.assertNotNull(workoutsViewModel.getSelectedWorkoutId());
        Assert.assertEquals("123", workoutsViewModel.getSelectedWorkoutId());
    }
}