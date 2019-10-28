package com.vbarjovanu.workouttimer.preferences;

import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemType;

public interface IWorkoutTrainingPreferences extends IBasePreferences {
    int getColor(WorkoutTrainingItemType type);

    void setColor(WorkoutTrainingItemType type, int color);

    boolean isIncreaseDuration();

    void setIncreaseDuration(boolean increased);
}
