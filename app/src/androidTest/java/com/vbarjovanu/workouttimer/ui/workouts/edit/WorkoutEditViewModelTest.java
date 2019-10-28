package com.vbarjovanu.workouttimer.ui.workouts.edit;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
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

    private IWorkoutEditViewModel workoutEditViewModel;

    private Observer<Workout> workoutObserver;

    private Observer<WorkoutEditFragmentAction> actionObserver;

    private CountDownLatch countDownLatch;

    @Before
    public void setup() {
        this.setupMockedAppSession();
        this.workoutsService = mock(IWorkoutsService.class);
        this.workoutEditViewModel = new WorkoutEditViewModel(this.applicationSession, this.workoutsService);
        this.setupWorkoutObserver();
        this.setupActionObserver();
        this.setupCountDownLatch();
    }

    private void setupCountDownLatch() {
        this.countDownLatch = new CountDownLatch(1);
        this.workoutEditViewModel.setCountDownLatch(countDownLatch);
    }

    private void setupWorkoutObserver() {
        if (this.workoutObserver != null) {
            this.workoutEditViewModel.getWorkout().removeObserver(this.workoutObserver);
        }
        //noinspection unchecked
        this.workoutObserver = mock(Observer.class);
        this.workoutEditViewModel.getWorkout().observeForever(this.workoutObserver);
    }

    private void setupActionObserver() {
        if (this.actionObserver != null) {
            this.workoutEditViewModel.getAction().removeObserver(this.actionObserver);
        }
        //noinspection unchecked
        this.actionObserver = mock(Observer.class);
        this.workoutEditViewModel.getAction().observeForever(this.actionObserver);
    }

    private void setupMockedAppSession() {
        this.applicationSession = mock(IApplicationSession.class);
        IWorkoutTimerPreferences workoutTimerPreferences = mock(IWorkoutTimerPreferences.class);
        IFileRepositoryPreferences fileRepositoryPreferences= mock(IFileRepositoryPreferences.class);
        Mockito.when(this.applicationSession.getWorkoutTimerPreferences()).thenReturn(workoutTimerPreferences);
        Mockito.when(workoutTimerPreferences.getFileRepositoryPreferences()).thenReturn(fileRepositoryPreferences);
        Mockito.when(fileRepositoryPreferences.getFolderPath()).thenReturn(folder.getRoot().getAbsolutePath());
        Mockito.when(this.applicationSession.getUserProfileId()).thenReturn("123");
    }

    @Test
    public void newWorkout() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        /*
         * call newWorkout()
         * expects: workout is changed and returns the newly created workout
         * expects: action is not triggered
         */
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<WorkoutEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);
        Workout newWorkout = new Workout("123-new");
        Mockito.when(this.workoutsService.createModel()).thenReturn(newWorkout);
        this.workoutEditViewModel.newWorkout();
        //workout change
        Mockito.verify(this.workoutObserver, Mockito.times(1)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNotNull(workout);
        Assert.assertEquals(newWorkout, workout);
        //no action change
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void loadWorkout() throws InterruptedException {
        /*
         * call loadWorkout() with a valid ID
         * expects: workout is changed and returns the loaded workout
         * expects: action is not triggered
         */
        String workoutId = "abc";
        String userProfileId = "123";
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<WorkoutEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);

        Workout newWorkout = new Workout(workoutId);
        Mockito.when(this.workoutsService.loadModel(userProfileId, workoutId)).thenReturn(newWorkout);
        this.workoutEditViewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        //workout change
        Mockito.verify(this.workoutObserver, Mockito.times(1)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNotNull(workout);
        Assert.assertEquals(newWorkout, workout);
        //no action change
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void loadWorkoutNonExistentId() throws InterruptedException {
        /*
         * call loadWorkout() with a valid ID
         * expects: workout is changed and returns null
         * expects: action is not triggered
         */
        String workoutId = "ab";
        String userProfileId = "123";
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<WorkoutEditFragmentAction> actionCaptor = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);

        Mockito.when(this.workoutsService.loadModel(userProfileId, workoutId)).thenReturn(null);
        this.workoutEditViewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        //workout change
        Mockito.verify(this.workoutObserver, Mockito.times(1)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNull(workout);
        //no action change
        Mockito.verify(this.actionObserver, Mockito.times(0)).onChanged(actionCaptor.capture());
    }

    @Test
    public void getWorkout() {
        /*
         * by default getWorkout returns a LiveData object with no content
         */
        LiveData<Workout> workout = this.workoutEditViewModel.getWorkout();
        Assert.assertNotNull(workout);
        Assert.assertNull(workout.getValue());
    }

    @Test
    public void saveWorkoutNoWorkoutLoaded() throws InterruptedException {
        /*
         * call saveWorkout() when no workout was loaded before
         * expects: workout is not changed and getWorkout() still returns no content
         * expects: action is triggered GOTO_WORKOUTS
         */
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<WorkoutEditFragmentAction> captorAction = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);
        Workout workoutToSave = new Workout("newId");
        workoutToSave.setName("name").setDescription("description");
        this.workoutEditViewModel.saveWorkout(workoutToSave);
        this.countDownLatch.await();
        //check workout is not changed
        Mockito.verify(this.workoutObserver, Mockito.times(0)).onChanged(captor.capture());
        Workout workout = this.workoutEditViewModel.getWorkout().getValue();
        Assert.assertNull(workout);
        //check action is triggered
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(captorAction.capture());
        assertNotNull(this.workoutEditViewModel.getAction().getValue());
        assertEquals(WorkoutEditFragmentAction.GOTO_WORKOUTS, this.workoutEditViewModel.getAction().getValue());
    }

    @Test
    public void saveWorkoutLoaded() throws InterruptedException {
        /*
         * call saveWorkout() when a workout was loaded before
         * expects: workout is changed and returns no updated content
         * expects: action is triggered GOTO_WORKOUTS
         */
        String workoutId = "abc";
        String userProfileId = "123";
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<WorkoutEditFragmentAction> captorAction = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);
        Workout newWorkout = new Workout(workoutId);
        Mockito.when(this.workoutsService.loadModel(userProfileId, workoutId)).thenReturn(newWorkout);
        this.workoutEditViewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        Workout workoutToSave = new Workout(workoutId);
        workoutToSave.setName("name").setDescription("description");
        this.setupCountDownLatch();
        this.workoutEditViewModel.saveWorkout(workoutToSave);
        this.countDownLatch.await();
        //check workout is not changed
        Mockito.verify(this.workoutObserver, Mockito.times(2)).onChanged(captor.capture());
        Workout workout = captor.getValue();
        Assert.assertNotNull(workout);
        Assert.assertEquals(newWorkout, workout);
        Assert.assertEquals("name", workout.getName());
        Assert.assertEquals("description", workout.getDescription());
        //check action is triggered
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(captorAction.capture());
        WorkoutEditFragmentAction action = captorAction.getValue();
        assertNotNull(action);
        assertEquals(WorkoutEditFragmentAction.GOTO_WORKOUTS, action);
    }

    @Test
    public void cancelWorkoutEditNothingLoaded() throws InterruptedException {
        /*
         * call cancelWorkoutEdit() when no workout was loaded before
         * expects: workout is not changed and getWorkout still returns null
         * expects: action is triggered GOTO_WORKOUTS
         */
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<WorkoutEditFragmentAction> captorAction = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);

        Assert.assertNull(this.workoutEditViewModel.getWorkout().getValue());
        this.workoutEditViewModel.cancelWorkoutEdit();
        this.countDownLatch.await();
        //check workout is not changed
        Mockito.verify(this.workoutObserver, Mockito.times(0)).onChanged(captor.capture());
        Assert.assertNull(this.workoutEditViewModel.getWorkout().getValue());
        //check action is triggered
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(captorAction.capture());
        WorkoutEditFragmentAction action = captorAction.getValue();
        assertNotNull(action);
        assertEquals(WorkoutEditFragmentAction.GOTO_WORKOUTS, action);
    }

    @Test
    public void cancelWorkoutEditWorkoutLoaded() throws InterruptedException {
        /*
         * call cancelWorkoutEdit() when a workout was loaded before
         * expects: workout is changed and returns null
         * expects: action is triggered GOTO_WORKOUTS
         */
        String workoutId = "abc";
        String userProfileId = "123";
        ArgumentCaptor<Workout> captor = ArgumentCaptor.forClass(Workout.class);
        ArgumentCaptor<WorkoutEditFragmentAction> captorAction = ArgumentCaptor.forClass(WorkoutEditFragmentAction.class);
        //load workout
        Workout newWorkout = new Workout(workoutId);
        Mockito.when(this.workoutsService.loadModel(userProfileId, workoutId)).thenReturn(newWorkout);
        this.workoutEditViewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        //cancel workout
        this.setupCountDownLatch();
        this.workoutEditViewModel.cancelWorkoutEdit();
        this.countDownLatch.await();
        //check workout is changed
        Mockito.verify(this.workoutObserver, Mockito.times(2)).onChanged(captor.capture());
        Workout workout = this.workoutEditViewModel.getWorkout().getValue();
        Assert.assertNull(workout);
        //check action is triggered
        Mockito.verify(this.actionObserver, Mockito.times(1)).onChanged(captorAction.capture());
        WorkoutEditFragmentAction action = captorAction.getValue();
        assertNotNull(action);
        assertEquals(WorkoutEditFragmentAction.GOTO_WORKOUTS, action);
    }

    @Test
    public void getAction() {
        /*
         * by default getAction returns an event with no content
         */
        SingleLiveEvent<WorkoutEditFragmentAction> action = this.workoutEditViewModel.getAction();
        assertNotNull(action);
    }
}