package com.vbarjovanu.workouttimer.ui.workouts.training;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.services.workouts.IWorkoutsService;
import com.vbarjovanu.workouttimer.preferences.IWorkoutPreferences;
import com.vbarjovanu.workouttimer.preferences.IWorkoutTimerPreferences;
import com.vbarjovanu.workouttimer.preferences.IWorkoutTrainingPreferences;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.workouts.training.logic.IWorkoutTrainingTimer;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemType;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ConstantConditions")
public class WorkoutTrainingViewModelTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private IWorkoutTrainingViewModel viewModel;
    private CountDownLatch countDownLatch;
    private Observer<? super WorkoutTrainingModel> workoutTrainingModelObserver;
    private IWorkoutTrainingTimer workoutTrainingTimer;

    @Before
    public void setUp() {
        IApplicationSession applicationSession;
        IWorkoutsService workoutsService;
        Workout workout;
        IWorkoutTimerPreferences workoutTimerPreferences;
        IWorkoutTrainingPreferences workoutTrainingPreferences;
        IWorkoutPreferences workoutPreferences;

        applicationSession = mock(IApplicationSession.class);
        workoutsService = mock(IWorkoutsService.class);
        this.workoutTrainingTimer = mock(IWorkoutTrainingTimer.class);
        workoutTimerPreferences = mock(IWorkoutTimerPreferences.class);
        workoutTrainingPreferences = mock(IWorkoutTrainingPreferences.class);
        workoutPreferences = mock(IWorkoutPreferences.class);
        //noinspection unchecked
        this.workoutTrainingModelObserver = mock(Observer.class);

        when(applicationSession.getUserProfileId()).thenReturn("profile-123");
        when(applicationSession.getWorkoutTimerPreferences()).thenReturn(workoutTimerPreferences);
        when(workoutTimerPreferences.getWorkoutPreferences()).thenReturn(workoutPreferences);
        when(workoutTimerPreferences.getWorkoutTrainingPreferences()).thenReturn(workoutTrainingPreferences);
        when(workoutPreferences.getIncludeLastRest()).thenReturn(false);
        when(workoutTrainingPreferences.getColor(any())).thenReturn(1);
        when(this.workoutTrainingTimer.start()).thenReturn(true);

        workout = new Workout("123").setPrepareDuration(5).setWorkDuration(10).setRestDuration(10).setCyclesCount(5).setSetsCount(1).setCoolDownDuration(5);
        when(workoutsService.loadModel("profile-123", "123")).thenReturn(workout);
        this.viewModel = new WorkoutTrainingViewModel(applicationSession, workoutsService, this.workoutTrainingTimer);
        this.countDownLatch = new CountDownLatch(1);
        this.viewModel.setCountDownLatch(this.countDownLatch);
        this.viewModel.getWorkoutTrainingModel().observeForever(this.workoutTrainingModelObserver);
    }

    @Test
    public void getWorkoutTrainingModel() {
        /*
         * by default the model LiveData exists but has no content
         */
        assertNotNull(this.viewModel.getWorkoutTrainingModel());
        assertNull(this.viewModel.getWorkoutTrainingModel().getValue());
    }

    @Test
    public void getWorkoutTrainingModelValidWorkout() throws InterruptedException {
        /*
         * call getWorkoutTrainingModel() after a valid workout was loaded
         * expects: the model LiveData exists and has content, for the desired workout
         * expects: the default values are: vibrate and sound on, in training and locked off
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        assertNotNull(this.viewModel.getWorkoutTrainingModel());
        assertNotNull(this.viewModel.getWorkoutTrainingModel().getValue());
        assertEquals(workoutId, this.viewModel.getWorkoutTrainingModel().getValue().getWorkout().getId());
        assertTrue(this.viewModel.getWorkoutTrainingModel().getValue().isVibrateOn());
        assertTrue(this.viewModel.getWorkoutTrainingModel().getValue().isSoundOn());
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isInTraining());
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isLocked());
    }

    @Test
    public void getAction() {
        /*
         * by default the action event exists but has no content
         */
        assertNotNull(this.viewModel.getAction());
        assertNull(this.viewModel.getAction().getValue());
    }

    @Test
    public void loadWorkout() throws InterruptedException {
        /*
         * call method loadWorkout() for a known workoutId
         * expects: no exceptions
         * expects: workoutTrainingModel is changed, is not null and it contains the desired workout
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        ArgumentCaptor<WorkoutTrainingModel> modelCaptor = ArgumentCaptor.forClass(WorkoutTrainingModel.class);
        verify(this.workoutTrainingModelObserver, times(1)).onChanged(modelCaptor.capture());
        assertNotNull(this.viewModel.getWorkoutTrainingModel().getValue());
        assertNotNull(this.viewModel.getWorkoutTrainingModel().getValue().getWorkout());
        assertEquals(workoutId, this.viewModel.getWorkoutTrainingModel().getValue().getWorkout().getId());
        assertNotNull(modelCaptor.getValue());
    }

    @Test
    public void loadWorkoutUnknownId() throws InterruptedException {
        /*
         * call method loadWorkout() for an unknown workoutId
         * expects: no exceptions
         * expects: workoutTrainingModel is not changed and remains null
         */
        String workoutId = "1234";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        ArgumentCaptor<WorkoutTrainingModel> modelCaptor = ArgumentCaptor.forClass(WorkoutTrainingModel.class);
        verify(this.workoutTrainingModelObserver, times(0)).onChanged(modelCaptor.capture());
        assertNull(this.viewModel.getWorkoutTrainingModel().getValue());
    }

    @Test
    public void startWorkoutTraining() {
        /*
         * call startWorkoutTraining() when no workout is loaded
         * expects: no exception
         * expects: workoutTrainingTimer.start() is not called
         */
        this.viewModel.startWorkoutTraining();
        verify(this.workoutTrainingTimer, times(0)).start();
    }

    @Test
    public void startWorkoutTrainingValidWorkout() throws InterruptedException {
        /*
         * call method startWorkoutTraining(), after a known workoutId was loaded
         * expects: no exceptions
         * expects: workoutTrainingModel.workoutTrainingTimer.start() is called twice (one during load and once by our direct call)
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(false);
        this.viewModel.startWorkoutTraining();
        verify(this.workoutTrainingTimer, times(2)).start();
    }

    @Test
    public void startWorkoutTrainingValidWorkoutButLocked() throws InterruptedException {
        /*
         * call method startWorkoutTraining(), after a known workoutId was loaded but is locked
         * expects: no exceptions
         * expects: workoutTrainingModel.workoutTrainingTimer.start() is called once (only once during load)
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(true);
        this.viewModel.startWorkoutTraining();
        verify(this.workoutTrainingTimer, times(1)).start();
    }

    @Test
    public void pauseWorkoutTraining() {
        /*
         * call pauseWorkoutTraining() when no workout is loaded
         * expects: no exception
         * expects: workoutTrainingTimer.pause() is not called
         */
        this.viewModel.pauseWorkoutTraining();
        verify(this.workoutTrainingTimer, times(0)).pause();
    }

    @Test
    public void pauseWorkoutTrainingValidWorkout() throws InterruptedException {
        /*
         * call method pauseWorkoutTraining(), after a known workoutId was loaded
         * expects: no exceptions
         * expects: workoutTrainingModel.workoutTrainingTimer.pause() is called once
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(false);
        this.viewModel.pauseWorkoutTraining();
        verify(this.workoutTrainingTimer, times(1)).pause();
    }

    @Test
    public void pauseWorkoutTrainingValidWorkoutButLocked() throws InterruptedException {
        /*
         * call method pauseWorkoutTraining(), after a known workoutId was loaded, but is locked
         * expects: no exceptions
         * expects: workoutTrainingModel.workoutTrainingTimer.pause() is not called
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(true);
        this.viewModel.pauseWorkoutTraining();
        verify(this.workoutTrainingTimer, times(0)).pause();
    }

    @Test
    public void stopWorkoutTraining() {
        /*
         * call stopWorkoutTraining() when no workout is loaded
         * expects: no exception
         * expects: workoutTrainingTimer.stop() is called once - tries to stop the timer in any instance
         */
        this.viewModel.stopWorkoutTraining();
        verify(this.workoutTrainingTimer, times(1)).stop();
    }

    @Test
    public void stopWorkoutTrainingValidWorkout() throws InterruptedException {
        /*
         * call method stopWorkoutTraining(), after a known workoutId was loaded
         * expects: no exceptions
         * expects: workoutTrainingModel.workoutTrainingTimer.stop() is called once
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        this.viewModel.stopWorkoutTraining();
        verify(this.workoutTrainingTimer, times(1)).stop();
    }

    @Test
    public void nextWorkoutTrainingItem() {
        /*
         * call nextWorkoutTrainingItem() when no workout is loaded
         * expects: no exception
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called
         */
        this.viewModel.nextWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(0)).start();
    }

    @Test
    public void nextWorkoutTrainingItemValidWorkout() throws InterruptedException {
        /*
         * call nextWorkoutTrainingItem(), after a known workoutId was loaded and not in training and not locked
         * expects: no exception
         * expects: initially training item type is PREPARE
         * expects: after calling next training item type is WORK
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called after load
         *
         * call nextWorkoutTrainingItem(), after a known workoutId was loaded and in training and not locked
         * expects: no exception
         * expects: isInTraining is true
         * expects: current training item type is WORK
         * expects: after calling next training item type is REST
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are called each one time
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(false);
        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(false);
        this.viewModel.nextWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(1)).start(); //one time is from the viewModel.loadworkout()
        assertEquals(WorkoutTrainingItemType.WORK, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());

        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(true);
        assertTrue(this.viewModel.getWorkoutTrainingModel().getValue().isInTraining());
        this.viewModel.nextWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(1)).stop();
        verify(this.workoutTrainingTimer, times(2)).start();
        assertEquals(WorkoutTrainingItemType.REST, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
    }

    @Test
    public void nextWorkoutTrainingItemValidWorkoutButLocked() throws InterruptedException {
        /*
         * call nextWorkoutTrainingItem(), after a known workoutId was loaded and not in training and locked
         * expects: no exception
         * expects: training item type remains PREPARE, as before the call
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called after load
         *
         * call nextWorkoutTrainingItem(), after a known workoutId was loaded and in training and locked
         * expects: no exception
         * expects: current training item type remains PREPARE
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called after load
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(true);
        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(false);
        this.viewModel.nextWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(1)).start(); //one time is from the viewModel.loadworkout()
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());

        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(true);
        this.viewModel.nextWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(1)).start();
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
    }

    @Test
    public void previousWorkoutTrainingItem() {
        /*
         * call previousWorkoutTrainingItem() when no workout is loaded
         * expects: no exception
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called
         */
        this.viewModel.previousWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(0)).start();
    }

    @Test
    public void previousWorkoutTrainingItemValidWorkout() throws InterruptedException {
        /*
         * call previousWorkoutTrainingItem(), after a known workoutId was loaded and not in training and not locked
         * expects: no exception
         * expects: initially training item type is PREPARE
         * expects: after calling next training item type remains PREPARE
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called after load
         *
         * call previousWorkoutTrainingItem(), after a known workoutId was loaded and in training and not locked
         * expects: no exception
         * expects: isInTraining is true
         * expects: current training item type is PREPARE
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are called each one time
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(false);
        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(false);
        this.viewModel.previousWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(1)).start(); //one time is from the viewModel.loadworkout()
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());

        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(true);
        assertTrue(this.viewModel.getWorkoutTrainingModel().getValue().isInTraining());
        this.viewModel.previousWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(1)).stop();
        verify(this.workoutTrainingTimer, times(2)).start();
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
    }

    @Test
    public void previousWorkoutTrainingItemValidWorkoutButLocked() throws InterruptedException {
        /*
         * call previousWorkoutTrainingItem(), after a known workoutId was loaded and not in training and locked
         * expects: no exception
         * expects: training item type remains PREPARE, as before the call
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called after load
         *
         * call previousWorkoutTrainingItem(), after a known workoutId was loaded and in training and locked
         * expects: no exception
         * expects: current training item type remains PREPARE
         * expects: workoutTrainingTimer.start() and workoutTrainingTimer.stop() are not called after load
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(true);
        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(false);
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
        this.viewModel.previousWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(1)).start(); //one time is from the viewModel.loadworkout()
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());

        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(true);
        this.viewModel.previousWorkoutTrainingItem();
        verify(this.workoutTrainingTimer, times(0)).stop();
        verify(this.workoutTrainingTimer, times(1)).start(); //one time is from the viewModel.loadworkout()
        assertEquals(WorkoutTrainingItemType.PREPARE, this.viewModel.getWorkoutTrainingModel().getValue().getCurrentWorkoutTrainingItem().getType());
    }

    @Test
    public void toggleLock() {
        /*
         * call toggleLock() when no workout is loaded
         * expects: no exception
         */
        this.viewModel.toggleLock();
    }

    @Test
    public void toggleLockValidWorkout() throws InterruptedException {
        /*
         * call toggleLock(), after a known workoutId was loaded
         * expects: no exception
         * expects: while is not in training, lock can't be changed
         * expects: while in training, lock can be changed
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isInTraining());
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isLocked());
        this.viewModel.toggleLock();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isLocked());

        this.viewModel.getWorkoutTrainingModel().getValue().setInTraining(true);
        this.viewModel.toggleLock();
        assertTrue(this.viewModel.getWorkoutTrainingModel().getValue().isLocked());
        this.viewModel.toggleLock();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isLocked());
    }

    @Test
    public void toggleSound() {
        /*
         * call toggleSound() when no workout is loaded
         * expects: no exception
         */
        this.viewModel.toggleSound();
    }

    @Test
    public void toggleSoundValidWorkout() throws InterruptedException {
         /*
         * call toggleSound(), after a known workoutId was loaded
         * expects: no exception
         * expects: while is locked, sound can't be changed
         * expects: while is not locked, sound can be changed
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isLocked());
        assertTrue(this.viewModel.getWorkoutTrainingModel().getValue().isSoundOn());
        this.viewModel.toggleSound();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isSoundOn());
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(true);
        this.viewModel.toggleSound();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isSoundOn());
    }

    @Test
    public void toggleVibrate() {
        /*
         * call toggleVibrate() when no workout is loaded
         * expects: no exception
         */
        this.viewModel.toggleVibrate();
    }

    @Test
    public void toggleVibrateValidWorkout() throws InterruptedException {
         /*
         * call toggleVibrate(), after a known workoutId was loaded
         * expects: no exception
         * expects: while is locked, vibrate can't be changed
         * expects: while is not locked, vibrate can be changed
         */
        String workoutId = "123";
        this.viewModel.loadWorkout(workoutId);
        this.countDownLatch.await();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isLocked());
        assertTrue(this.viewModel.getWorkoutTrainingModel().getValue().isVibrateOn());
        this.viewModel.toggleVibrate();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isVibrateOn());
        this.viewModel.getWorkoutTrainingModel().getValue().setLocked(true);
        this.viewModel.toggleVibrate();
        assertFalse(this.viewModel.getWorkoutTrainingModel().getValue().isVibrateOn());
    }

}