package com.vbarjovanu.workouttimer.preferences;

import android.content.Context;

import androidx.annotation.NonNull;

public class WorkoutTimerPreferences extends BasePreferences implements IWorkoutTimerPreferences {
    private static final String KeyUserProfileId = "UserProfileId";

    public WorkoutTimerPreferences(@NonNull Context context) {
        super(context);
    }

    @Override
    public String getUserProfileId() {
        return this.sharedPreferences.getString(KeyUserProfileId, null);
    }

    @Override
    public void setUserProfileId(String userProfileId) {
        this.sharedPreferences.edit().putString(KeyUserProfileId, userProfileId).apply();
    }

    @Override
    public IFileRepositoryPreferences getFileRepositoryPreferences() {
        return new FileRepositoryPreferences(this.context);
    }

    @Override
    public IWorkoutPreferences getWorkoutPreferences() {
        return new WorkoutPreferences(this.context);
    }

    @Override
    public IWorkoutTrainingPreferences getWorkoutTrainingPreferences() {
        return new WorkoutTrainingPreferences(this.context);
    }

    @Override
    public void clear() {
        super.clear();
        this.getFileRepositoryPreferences().clear();
        this.getWorkoutPreferences().clear();
        this.getWorkoutTrainingPreferences().clear();
    }
}
