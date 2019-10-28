package com.vbarjovanu.workouttimer.preferences;

public interface IWorkoutPreferences extends IBasePreferences {
    boolean getIncludeLastRest();

    void setIncludeLastRest(boolean includeLastRest);
}
