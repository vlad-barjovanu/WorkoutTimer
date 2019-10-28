package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkoutTrainingItemModelTest {
    private WorkoutTrainingItemModel modelIncrease;
    private WorkoutTrainingItemModel modelDecrease;

    @Before
    public void setUp() {
        this.modelIncrease = new WorkoutTrainingItemModel(WorkoutTrainingItemType.PREPARE, 10, true, 3, 2, 1, "item description");
        this.modelDecrease = new WorkoutTrainingItemModel(WorkoutTrainingItemType.PREPARE, 10, false, 3, 2, 1, "item description");
    }

    @After
    public void tearDown() {
        this.modelIncrease = null;
        this.modelDecrease = null;
    }

    @Test
    public void getType() {
        assertEquals(WorkoutTrainingItemType.PREPARE, this.modelIncrease.getType());
    }

    @Test
    public void getDuration() {
        assertEquals(0, this.modelIncrease.getDuration());
        assertEquals(10, this.modelDecrease.getDuration());
    }

    @Test
    public void getDescription() {
        assertEquals("item description", this.modelIncrease.getDescription());
    }

    @Test
    public void getTotalIndex() {
        assertEquals(3, this.modelIncrease.getTotalIndex());
    }

    @Test
    public void getCycleIndex() {
        assertEquals(2, this.modelIncrease.getCycleIndex());
    }

    @Test
    public void getSetIndex() {
        assertEquals(1, this.modelIncrease.getSetIndex());
    }

    @Test
    public void resetDuration() {
        assertEquals(0, this.modelIncrease.resetDuration());
        assertEquals(10, this.modelDecrease.resetDuration());
    }

    @Test
    public void resetDurationAfterAlter() {
        assertEquals(1, this.modelIncrease.alterDuration());
        assertEquals(0, this.modelIncrease.resetDuration());
        assertEquals(9, this.modelDecrease.alterDuration());
        assertEquals(10, this.modelDecrease.resetDuration());
    }

    @Test
    public void alterDuration() {
        for (int i = 1; i <= 10; i++) {
            assertEquals(i, this.modelIncrease.alterDuration());
            assertEquals(10 - i, this.modelDecrease.alterDuration());
        }
        // one extra alter to make sure it doesn't go beyond limits
        this.modelIncrease.alterDuration();
        this.modelDecrease.alterDuration();
        assertEquals(10, this.modelIncrease.getDuration());
        assertEquals(0, this.modelDecrease.getDuration());
    }

    @Test
    public void isComplete() {
        assertFalse(this.modelIncrease.isComplete());
        assertFalse(this.modelDecrease.isComplete());
        for (int i = 0; i < 10; i++) {
            this.modelIncrease.alterDuration();
            this.modelDecrease.alterDuration();
        }
        assertTrue(this.modelIncrease.isComplete());
        assertTrue(this.modelDecrease.isComplete());

        // one extra alter to make sure it doesn't go beyond limits
        this.modelIncrease.alterDuration();
        this.modelDecrease.alterDuration();
        assertTrue(this.modelIncrease.isComplete());
        assertTrue(this.modelDecrease.isComplete());
    }
}