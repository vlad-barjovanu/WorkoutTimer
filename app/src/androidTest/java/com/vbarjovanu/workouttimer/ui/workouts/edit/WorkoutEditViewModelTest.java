package com.vbarjovanu.workouttimer.ui.workouts.edit;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.workouts.list.WorkoutsFragmentAction;

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
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;

public class WorkoutEditViewModelTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    private IWorkoutsService workoutsService;

    @Mock
    private IApplicationSession applicationSession;

    private WorkoutEditViewModel workoutEditViewModel;

    private Observer<Workout> observer;

    private Observer<WorkoutEditFragmentAction> observerAction;

    private CountDownLatch countDownLatch;

    @Before
    public void setup() {
        this.applicationSession = mock(IApplicationSession.class);
        this.workoutsService = mock(IWorkoutsService.class);
        this.setupMockedAppSession();
        this.workoutEditViewModel = new WorkoutEditViewModel(this.applicationSession, this.workoutsService);
        //noinspection unchecked
        this.observer = mock(Observer.class);
        //noinspection unchecked
        this.observerAction = mock(Observer.class);
        this.workoutEditViewModel.getWorkout().observeForever(this.observer);
        this.workoutEditViewModel.getAction().observeForever(this.observerAction);
        //load data and check if observer's onChanged method was triggered
        this.countDownLatch = new CountDownLatch(1);
        this.workoutEditViewModel.setCountDownLatch(countDownLatch);
    }

    private void setupMockedAppSession(){
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn("123");
        Mockito.when(this.applicationSession.getFileRepositoriesFolderPath()).thenReturn(folder.getRoot().getAbsolutePath());
    }

    @Test
    public void newWorkout() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Workout newWorkout = new Workout("123-new");
        Mockito.when(this.workoutsService.createModel()).thenReturn(newWorkout);
        this.workoutEditViewModel.newWorkout();
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNotNull(workout);
        Assert.assertEquals(newWorkout, workout);

    }

    @Test
    public void loadWorkout() throws InterruptedException {
        Workout newWorkout = new Workout("abc");
        Mockito.when(this.workoutsService.loadModel("123", "abc")).thenReturn(newWorkout);
        this.workoutEditViewModel.loadWorkout("abc");
        this.countDownLatch.await();
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNotNull(workout);
        Assert.assertEquals(newWorkout, workout);
    }

    @Test
    public void getWorkout() {
        LiveData<Workout> workout= this.workoutEditViewModel.getWorkout();
        Assert.assertNotNull(workout);
        Assert.assertNull(workout.getValue());
    }

    @Test
    public void saveWorkoutNoWorkoutLoaded() throws InterruptedException {
        Workout workoutToSave = new Workout("newId");
        workoutToSave.setName("name").setDescription("description");
        this.workoutEditViewModel.saveWorkout(workoutToSave);
        this.countDownLatch.await(1, TimeUnit.SECONDS);
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        Mockito.verify(this.observer, Mockito.times(0)).onChanged(captor.capture());
        Workout workout = this.workoutEditViewModel.getWorkout().getValue();
        Assert.assertNull(workout);
        ArgumentCaptor<WorkoutEditFragmentAction> captorAction = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);
        Mockito.verify(this.observerAction, Mockito.times(0)).onChanged(captorAction.capture());
        assertNull(this.workoutEditViewModel.getAction().getValue());
    }

    @Test
    public void saveWorkoutLoaded() throws InterruptedException {
        Workout newWorkout = new Workout("abc");
        Mockito.when(this.workoutsService.loadModel("123", "abc")).thenReturn(newWorkout);
        this.workoutEditViewModel.loadWorkout("abc");
        this.countDownLatch.await();
        Workout workoutToSave = new Workout("abc");
        workoutToSave.setName("name").setDescription("description");
        this.countDownLatch = new CountDownLatch(1);
        this.workoutEditViewModel.setCountDownLatch(countDownLatch);
        this.workoutEditViewModel.saveWorkout(workoutToSave);
        this.countDownLatch.await();
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        Mockito.verify(this.observer, Mockito.times(2)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNotNull(workout);
        Assert.assertEquals(newWorkout, workout);
        Assert.assertEquals("name", workout.getName());
        Assert.assertEquals("description", workout.getDescription());
        ArgumentCaptor<WorkoutEditFragmentAction> captorAction = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);
        Mockito.verify(this.observerAction, Mockito.times(1)).onChanged(captorAction.capture());
        WorkoutEditFragmentAction action = captorAction.getValue();
        assertNotNull(action);
        assertEquals(WorkoutEditFragmentAction.GOTO_WORKOUTS, action);
    }

    @Test
    public void cancelWorkoutEditNothingLoaded() throws InterruptedException {
        this.workoutEditViewModel.cancelWorkoutEdit();
        this.countDownLatch.await(1, TimeUnit.SECONDS);
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        Mockito.verify(this.observer, Mockito.times(1)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNull(workout);
        ArgumentCaptor<WorkoutEditFragmentAction> captorAction = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);
        Mockito.verify(this.observerAction, Mockito.times(1)).onChanged(captorAction.capture());
        WorkoutEditFragmentAction action = captorAction.getValue();
        assertNotNull(action);
        assertEquals(WorkoutEditFragmentAction.GOTO_WORKOUTS, action);
    }

    @Test
    public void cancelWorkoutEditWorkoutLoaded() throws InterruptedException {
        Workout newWorkout = new Workout("abc");
        Mockito.when(this.workoutsService.loadModel("123", "abc")).thenReturn(newWorkout);
        this.workoutEditViewModel.loadWorkout("abc");
        this.countDownLatch.await();
        this.countDownLatch = new CountDownLatch(1);
        this.workoutEditViewModel.setCountDownLatch(countDownLatch);
        this.workoutEditViewModel.cancelWorkoutEdit();
        this.countDownLatch.await(1, TimeUnit.SECONDS);
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        Mockito.verify(this.observer, Mockito.times(2)).onChanged(captor.capture());
        Workout workout = this.workoutEditViewModel.getWorkout().getValue();
        Assert.assertNull(workout);
    }

    @Test
    public void getAction() {
        SingleLiveEvent<WorkoutEditFragmentAction> action =  this.workoutEditViewModel.getAction();
        assertNotNull(action);
    }
}