package com.vbarjovanu.workouttimer.ui.workouts.list;

public class WorkoutsFragmentActionData {
    private final WorkoutsFragmentAction workoutsFragmentAction;
    private final String profileId;
    private final String workoutId;

    WorkoutsFragmentActionData(WorkoutsFragmentAction workoutsFragmentAction, String profileId, String workoutId) {
        this.workoutsFragmentAction = workoutsFragmentAction;
        this.profileId = profileId;
        this.workoutId = workoutId;
    }

    public WorkoutsFragmentAction getAction() {
        return workoutsFragmentAction;
    }

    public String getProfileId() {
        return profileId;
    }

    @SuppressWarnings("WeakerAccess")
    public String getWorkoutId() {
        return workoutId;
    }
}
