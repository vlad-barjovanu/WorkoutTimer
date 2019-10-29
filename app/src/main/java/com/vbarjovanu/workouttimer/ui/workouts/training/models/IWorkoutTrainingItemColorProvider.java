package com.vbarjovanu.workouttimer.ui.workouts.training.models;

public interface IWorkoutTrainingItemColorProvider {
    /**
     * Returns the color code for a certain WorkoutTrainingItemType
     *
     * @param itemType type of the workout training item
     * @return color value
     */
    int getColor(WorkoutTrainingItemType itemType);
}
