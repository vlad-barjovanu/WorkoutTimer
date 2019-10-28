package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;

import org.junit.After;
import org.junit.Before;
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
        HashMap<WorkoutTrainingItemType, Integer> colors;
        colors = new HashMap<>();
        colors.put(WorkoutTrainingItemType.PREPARE, 0);
        colors.put(WorkoutTrainingItemType.WORK, 1);
        colors.put(WorkoutTrainingItemType.REST, 2);
        colors.put(WorkoutTrainingItemType.SET_REST, 3);
        colors.put(WorkoutTrainingItemType.COOL_DOWN, 4);
        return new WorkoutTrainingModel(workout, includeLastRest, colors);
    }

    @Test
    public void getTotalDuration() {
        /*
         * all values are set
         */
        WorkoutTrainingModel model;
        model = this.buildDefaultWorkoutTrainingModel(this.buildDefaultWorkout(), true);
        assertEquals(345, model.getTotalDuration());
        model = this.buildDefaultWorkoutTrainingModel(this.buildDefaultWorkout(), false);
        assertEquals(310, model.getTotalDuration());
    }

    @Test
    public void getTotalDurationWithoutRest() {
        /*
         * rest value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setRestDuration(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(270, model.getTotalDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(250, model.getTotalDuration());
    }

    @Test
    public void getTotalDurationWithoutWork() {
        /*
         * work value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setWorkDuration(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(195, model.getTotalDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(160, model.getTotalDuration());
    }

    @Test
    public void getTotalDurationWithoutCyclesCount() {
        /*
         * cycles count value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setCyclesCount(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(120, model.getTotalDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(100, model.getTotalDuration());
    }

    @Test
    public void getTotalDurationWithoutSetsCount() {
        /*
         * sets count value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setSetsCount(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(60, model.getTotalDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(60, model.getTotalDuration());
    }

    @Test
    public void getTotalDurationWithoutPrepareAndCoolDownDuration() {
        /*
         * sets count value is missing - assumed 0
         */
        WorkoutTrainingModel model;
        Workout workout = this.buildDefaultWorkout().setPrepareDuration(null).setCoolDownDuration(null);
        model = this.buildDefaultWorkoutTrainingModel(workout, true);
        assertEquals(285, model.getTotalDuration());
        model = this.buildDefaultWorkoutTrainingModel(workout, false);
        assertEquals(250, model.getTotalDuration());
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
}