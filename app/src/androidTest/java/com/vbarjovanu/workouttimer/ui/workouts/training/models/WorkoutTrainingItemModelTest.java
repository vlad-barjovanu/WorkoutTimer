package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkoutTrainingItemModelTest {
    private WorkoutTrainingItemModel model;

    @Before
    public void setUp() {
        this.model = new WorkoutTrainingItemModel(WorkoutTrainingItemType.PREPARE, 10, 3, 2, 1, "item description");
    }

    @After
    public void tearDown() {
        this.model = null;
    }

    @Test
    public void getType() {
        assertEquals(WorkoutTrainingItemType.PREPARE, this.model.getType());
    }

    @Test
    public void getDuration() {
        assertEquals(0, this.model.getDuration());
    }

    @Test
    public void getDescription() {
        assertEquals("item description", this.model.getDescription());
    }

    @Test
    public void getTotalIndex() {
        assertEquals(3, this.model.getTotalIndex());
    }

    @Test
    public void getCycleIndex() {
        assertEquals(2, this.model.getCycleIndex());
    }

    @Test
    public void getSetIndex() {
        assertEquals(1, this.model.getSetIndex());
    }

    @Test
    public void resetDuration() {
        assertEquals(0, this.model.resetDuration());
    }

    @Test
    public void resetDurationAfterAlter() {
        assertEquals(1, this.model.alterDuration());
        assertEquals(0, this.model.resetDuration());
    }

    @Test
    public void alterDuration() {
        for (int i = 1; i <= 10; i++) {
            assertEquals(i, this.model.alterDuration());
        }
        // one extra alter to make sure it doesn't go beyond limits
        this.model.alterDuration();
        assertEquals(10, this.model.getDuration());
    }

    @Test
    public void isComplete() {
        assertFalse(this.model.isComplete());
        for (int i = 0; i < 10; i++) {
            this.model.alterDuration();
        }
        assertTrue(this.model.isComplete());

        // one extra alter to make sure it doesn't go beyond limits
        this.model.alterDuration();
        assertTrue(this.model.isComplete());
    }
}