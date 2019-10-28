package com.vbarjovanu.workouttimer.preferences;

import android.content.Context;

public class WorkoutPreferences extends BasePreferences implements IWorkoutPreferences {
    private static final String KeyIncludeLastRest = "IncludeLastRest";

    WorkoutPreferences(Context context) {
        super(context);
    }

    @Override
    public boolean getIncludeLastRest() {
        return this.sharedPreferences.getBoolean(KeyIncludeLastRest, false);
    }

    @Override
    public void setIncludeLastRest(boolean includeLastRest) {
        this.sharedPreferences.edit().putBoolean(KeyIncludeLastRest, includeLastRest).apply();
    }
}
