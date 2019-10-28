package com.vbarjovanu.workouttimer.session;

import androidx.annotation.NonNull;

import com.vbarjovanu.workouttimer.preferences.IWorkoutTimerPreferences;

import java.io.File;
import java.io.FileNotFoundException;

class ApplicationSession implements IApplicationSession {

    @NonNull
    private IWorkoutTimerPreferences workoutTimerPreferences;

    ApplicationSession(@NonNull IWorkoutTimerPreferences workoutTimerPreferences) {
        this.workoutTimerPreferences = workoutTimerPreferences;
    }

    @Override
    public void setUserProfileId(String id) {
        this.workoutTimerPreferences.setUserProfileId(id);
    }

    @Override
    public String getUserProfileId() {
        return this.workoutTimerPreferences.getUserProfileId();
    }

    @Override
    @NonNull
    public IWorkoutTimerPreferences getWorkoutTimerPreferences() {
        return this.workoutTimerPreferences;
    }
}
