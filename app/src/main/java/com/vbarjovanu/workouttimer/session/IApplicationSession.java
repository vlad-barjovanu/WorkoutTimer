package com.vbarjovanu.workouttimer.session;

import androidx.annotation.NonNull;

import com.vbarjovanu.workouttimer.preferences.IWorkoutTimerPreferences;

import java.io.FileNotFoundException;

public interface IApplicationSession {
    /**
     * Sets the user profile's ID
     *
     * @param id user profile's ID
     */
    void setUserProfileId(String id);

    /**
     * Returns the user profile ID
     *
     * @return user profile's ID
     */
    String getUserProfileId();

    /**
     * Returns the application preferences
     *
     * @return IWorkoutTimerPreferences
     */
    @NonNull
    IWorkoutTimerPreferences getWorkoutTimerPreferences();
}
