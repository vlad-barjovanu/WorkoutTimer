package com.vbarjovanu.workouttimer.business.models.workouts;

import com.vbarjovanu.workouttimer.business.models.generic.ModelsList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkoutsListTest {
    @Test
    public void fromArray() throws IllegalAccessException, InstantiationException {
        Workout[] workouts;
        WorkoutsList list;

        workouts = new Workout[2];
        workouts[0] = new Workout("123");
        workouts[1] = new Workout("456");
        list = WorkoutsList.fromArray(workouts, WorkoutsList.class);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("123", list.get(0).getId());
        assertEquals("456", list.get(1).getId());
    }

    @Test
    public void fromArrayNotUniqe() throws IllegalAccessException, InstantiationException {
        Workout[] workouts;
        WorkoutsList list;

        workouts = new Workout[2];
        workouts[0] = new Workout("123");
        workouts[1] = new Workout("123");
        list = WorkoutsList.fromArray(workouts, WorkoutsList.class);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("123", list.get(0).getId());
    }

    @Test
    public void find() {
        WorkoutsList list;
        Workout workout, workout1, workout2;

        list = new WorkoutsList();
        workout1 = new Workout("123");
        list.add(workout1);
        workout2 = new Workout("456");
        list.add(workout2);
        workout = list.find("123");
        assertNotNull(workout);
        assertEquals(workout1, workout);
        workout = list.find("456");
        assertNotNull(workout);
        assertEquals(workout2, workout);
    }
}