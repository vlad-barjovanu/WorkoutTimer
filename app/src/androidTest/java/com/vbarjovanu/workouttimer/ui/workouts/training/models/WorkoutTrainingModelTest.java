package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class WorkoutTrainingModelTest {

    private Workout buildDefaultWorkout() {
        String id = "1";
        Workout workout = new Workout(id);
        workout.setPrepareDuration(30)
                .setWorkDuration(10)
                .setRestDuration(5)
                .setCyclesCount(5)
                .setSetsCount(3)
                .setRestBetweenSetsDuration(20)
                .setCoolDownDuration(30);

        return workout;
    }

    private WorkoutTrainingModel buildDefaultWorkoutTrainingModel(Workout workout, boolean includeLastRest) {
        return new WorkoutTrainingModel(workout, includeLastRest);
    }

    @Test
    public void getTotalInitialDuration() {
        /*
         * all values are set
         */
        WorkoutTrainingModel model;
        model = this.buildDefaultWorkoutTrainingModel(this.buildDefaultWorkout(), true);
        assertEquals(345, model.getTotalInitialDuration());
        model = this.buildDefaultWorkoutTrainingModel(this.buildDefaultWorkout(), false);
        assertEquals(310, model.getTotalInitialDuration());
    }

    @Test
    public void getTotalInitialDurationWithoutRest() {
        /*
         * rest value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setRestDuration(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(270, model.getTotalInitialDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(250, model.getTotalInitialDuration());
    }

    @Test
    public void getTotalInitialDurationWithoutWork() {
        /*
         * work value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setWorkDuration(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(195, model.getTotalInitialDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(160, model.getTotalInitialDuration());
    }

    @Test
    public void getTotalInitialDurationWithoutCyclesCount() {
        /*
         * cycles count value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setCyclesCount(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(120, model.getTotalInitialDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(100, model.getTotalInitialDuration());
    }

    @Test
    public void getTotalInitialDurationWithoutSetsCount() {
        /*
         * sets count value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setSetsCount(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(60, model.getTotalInitialDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(60, model.getTotalInitialDuration());
    }

    @Test
    public void getTotalInitialDurationWithoutPrepareAndCoolDownDuration() {
        /*
         * sets count value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setPrepareDuration(null).setCoolDownDuration(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(285, model.getTotalInitialDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(250, model.getTotalInitialDuration());
    }

    @Test
    public void getTotalRemainingDuration() {
        /*
         * all values are set
         */
        WorkoutTrainingModel model;
        Workout workout;
        workout = this.buildDefaultWorkout();
        workout.setIncreaseDuration(false);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(345, model.getTotalRemainingDuration());
        model.getCurrentWorkoutTrainingItem().alterDuration();
        assertEquals(344, model.getTotalRemainingDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(310, model.getTotalRemainingDuration());
        model.getCurrentWorkoutTrainingItem().alterDuration();
        assertEquals(309, model.getTotalRemainingDuration());


        workout.setIncreaseDuration(true);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(345, model.getTotalRemainingDuration());
        model.getCurrentWorkoutTrainingItem().alterDuration();
        assertEquals(344, model.getTotalRemainingDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(310, model.getTotalRemainingDuration());
        model.getCurrentWorkoutTrainingItem().alterDuration();
        assertEquals(309, model.getTotalRemainingDuration());
    }

    @Test
    public void getWorkoutTrainingItems() {
        WorkoutTrainingModel model;
        List<WorkoutTrainingItemModel> items;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        items = model.getWorkoutTrainingItems();
        assertNotNull(items);
        assertEquals(35, items.size());
        assertEquals(WorkoutTrainingItemType.PREPARE, items.get(0).getType());
        assertEquals(WorkoutTrainingItemType.WORK, items.get(1).getType());
        assertEquals(WorkoutTrainingItemType.REST, items.get(2).getType());
        assertEquals(WorkoutTrainingItemType.WORK, items.get(9).getType());
        assertEquals(WorkoutTrainingItemType.REST, items.get(10).getType());
        assertEquals(WorkoutTrainingItemType.SET_REST, items.get(11).getType());
        assertEquals(WorkoutTrainingItemType.WORK, items.get(31).getType());
        assertEquals(WorkoutTrainingItemType.REST, items.get(32).getType());
        assertEquals(WorkoutTrainingItemType.SET_REST, items.get(33).getType());
        assertEquals(WorkoutTrainingItemType.COOL_DOWN, items.get(34).getType());

        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        items = model.getWorkoutTrainingItems();
        assertNotNull(items);
        assertEquals(31, items.size());
        assertEquals(WorkoutTrainingItemType.PREPARE, items.get(0).getType());
        assertEquals(WorkoutTrainingItemType.WORK, items.get(1).getType());
        assertEquals(WorkoutTrainingItemType.REST, items.get(2).getType());
        assertEquals(WorkoutTrainingItemType.WORK, items.get(9).getType());
        assertEquals(WorkoutTrainingItemType.SET_REST, items.get(10).getType());
        assertEquals(WorkoutTrainingItemType.WORK, items.get(11).getType());
        assertEquals(WorkoutTrainingItemType.WORK, items.get(29).getType());
        assertEquals(WorkoutTrainingItemType.COOL_DOWN, items.get(30).getType());
    }

    @Test
    public void getCurrentWorkoutTrainingItem() {
        WorkoutTrainingModel model;
        WorkoutTrainingItemModel item;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        item = model.getCurrentWorkoutTrainingItem();
        assertNotNull(item);
        assertEquals(WorkoutTrainingItemType.PREPARE, item.getType());
    }

    @Test
    public void nextTrainingItem() {
        WorkoutTrainingModel model;
        WorkoutTrainingItemModel item;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertTrue(model.nextTrainingItem());
    }

    @Test
    public void previousTrainingItem() {
        WorkoutTrainingModel model;
        WorkoutTrainingItemModel item;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertFalse(model.previousTrainingItem());
        assertTrue(model.nextTrainingItem());
        assertTrue(model.previousTrainingItem());
    }

    @Test
    public void goToTrainingItem() {
        WorkoutTrainingModel model;
        WorkoutTrainingItemModel item;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertTrue(model.goToTrainingItem(34));
        assertFalse(model.goToTrainingItem(35));
    }

    @Test
    public void setInTraining() {
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        model.setInTraining(false);
        assertFalse(model.isInTraining());
        model.setInTraining(true);
        assertTrue(model.isInTraining());
    }

    @Test
    public void setLocked() {
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        model.setLocked(false);
        assertFalse(model.isLocked());
        model.setLocked(true);
        assertTrue(model.isLocked());
    }

    @Test
    public void setSoundOn() {
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        model.setSoundOn(false);
        assertFalse(model.isSoundOn());
        model.setSoundOn(true);
        assertTrue(model.isSoundOn());
    }

    @Test
    public void setVibrateOn() {
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout();
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        model.setVibrateOn(false);
        assertFalse(model.isVibrateOn());
        model.setVibrateOn(true);
        assertTrue(model.isVibrateOn());
    }
}