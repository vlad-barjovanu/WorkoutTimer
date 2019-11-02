package com.vbarjovanu.workouttimer.ui.workouts.training.actions;

public class DurationChangeActionData extends WorkoutTrainingActionData {
    private final boolean playSound;
    private final boolean vibrate;

    public DurationChangeActionData(WorkoutTrainingActions action, boolean playSound, boolean vibrate) {
        super(action);
        this.playSound = playSound;
        this.vibrate = vibrate;
        if (action != WorkoutTrainingActions.MARK_DURATION_CHANGE && action != WorkoutTrainingActions.MARK_START_REST && action != WorkoutTrainingActions.MARK_START_WORK && action != WorkoutTrainingActions.MARK_TRAINING_COMPLETE) {
            throw new IllegalArgumentException(String.format("%s", action));
        }
    }

    public boolean isPlaySound() {
        return playSound;
    }

    public boolean isVibrate() {
        return vibrate;
    }
}
