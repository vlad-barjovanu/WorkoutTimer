package com.vbarjovanu.workouttimer.ui.workouts.training.logic;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemType;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static org.junit.Assert.*;

public class WorkoutTrainingTimerTest {
    private WorkoutTrainingTimer workoutTrainer;

    @Before
    public void setUp() {
        this.workoutTrainer = new WorkoutTrainingTimer();
    }

    @After
    public void tearDown() {
        if (this.workoutTrainer != null) {
            this.workoutTrainer.stop();
        }
        this.workoutTrainer = null;
    }

    private Workout buildDefaultWorkout() {
        String id = "1";
        Workout workout = new Workout(id);
        workout.setPrepareDuration(4)
                .setWorkDuration(10)
                .setRestDuration(5)
                .setCyclesCount(5)
                .setSetsCount(3)
                .setRestBetweenSetsDuration(20)
                .setCoolDownDuration(30)
                .setIncreaseDuration(true);

        return workout;
    }

    private WorkoutTrainingModel buildDefaultWorkoutTrainingModel() {
        return new WorkoutTrainingModel(this.buildDefaultWorkout(), true);
    }

    @Test
    public void loadWorkout() {
        this.workoutTrainer.loadWorkout(this.buildDefaultWorkoutTrainingModel());
    }

    @Test
    public void start() {
        /*
         * by default there's no workout model loaded and it can't start
         */
        assertFalse(this.workoutTrainer.start());
    }

    @Test
    public void startAfterModelLoaded() {
        /*
         * load a workout model and call start()
         * expects: returns true
         */
        this.workoutTrainer.loadWorkout(this.buildDefaultWorkoutTrainingModel());
        assertTrue(this.workoutTrainer.start());
    }

    @Test
    public void stop() {
        /*
         * by default it's not started and it can't stop
         */
        assertFalse(this.workoutTrainer.stop());
    }

    @Test
    public void stopAfterStart() {
        /*
         * load a workout model start and call stop()
         * expects: returns true
         */
        this.workoutTrainer.loadWorkout(this.buildDefaultWorkoutTrainingModel());
        this.workoutTrainer.start();
        assertTrue(this.workoutTrainer.stop());
    }

    @Test
    public void pause() {
    }

    @Test
    public void runLogicNotInTraining() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        /*
         * load a workout model and calls the logic by directly calling private method run()
         * 1st time is called 3 times
         * expects: current workout item remains the PREPARE and duration is increased up to 3
         * 2nd time is called 2 times
         * expects: current workout item is now WORK and duration is increased up to 1
         */
        WorkoutTrainingModel model = this.buildDefaultWorkoutTrainingModel();
        model.setInTraining(false);
        this.workoutTrainer.loadWorkout(model);

        Class<WorkoutTrainingTimer> workoutTrainerClass = WorkoutTrainingTimer.class;
        Method methodRun = workoutTrainerClass.getDeclaredMethod("run");
        methodRun.setAccessible(true);
        for (int i = 0; i < 3; i++) {
            methodRun.invoke(this.workoutTrainer);
        }
        assertEquals(0, model.getCurrentWorkoutTrainingItem().getDuration());
    }

    @Test
    public void runLogic() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        /*
         * load a workout model and calls the logic by directly calling private method run()
         * 1st time is called 3 times
         * expects: current workout item remains the PREPARE and duration is increased up to 3
         * 2nd time is called 2 times
         * expects: current workout item is now WORK and duration is increased up to 1
         */
        WorkoutTrainingModel model = this.buildDefaultWorkoutTrainingModel();
        model.setInTraining(true);
        this.workoutTrainer.loadWorkout(model);

        Class<WorkoutTrainingTimer> workoutTrainerClass = WorkoutTrainingTimer.class;
        Method methodRun = workoutTrainerClass.getDeclaredMethod("run");
        methodRun.setAccessible(true);
        for (int i = 0; i < 3; i++) {
            methodRun.invoke(this.workoutTrainer);
        }
        assertEquals(3, model.getCurrentWorkoutTrainingItem().getDuration());
        assertEquals(WorkoutTrainingItemType.PREPARE, model.getCurrentWorkoutTrainingItem().getType());
        for (int i = 0; i < 2; i++) {
            methodRun.invoke(this.workoutTrainer);
        }
        assertEquals(1, model.getCurrentWorkoutTrainingItem().getDuration());
        assertEquals(WorkoutTrainingItemType.WORK, model.getCurrentWorkoutTrainingItem().getType());
    }

}