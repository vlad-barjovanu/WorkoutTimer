package com.vbarjovanu.workouttimer.ui.workouts.training.actions;

import com.vbarjovanu.workouttimer.ui.workouts.training.actions.WorkoutTrainingActions;

public class WorkoutTrainingActionData {
    private final WorkoutTrainingActions action;

    WorkoutTrainingActionData(WorkoutTrainingActions action) {
        this.action = action;
    }

    public WorkoutTrainingActions getAction() {
        return action;
    }
}
