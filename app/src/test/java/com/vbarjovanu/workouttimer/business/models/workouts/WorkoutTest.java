package com.vbarjovanu.workouttimer.business.models.workouts;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkoutTest {
    @Test
    public void testWorkout() {
        String id = "1";
        Workout workout = new Workout(id);
        Assert.assertEquals(id, workout.getId());
    }

    @Test
    public void update() {
        String id = "1";
        String name = "workout1";
        Workout workout = new Workout(id);
        workout.setName(name);
        Workout workout2 = new Workout("2");
        workout2.setName("workout2");
        workout2.update(workout);
        Assert.assertEquals(id, workout2.getId());
        Assert.assertEquals(name, workout2.getName());
    }
}