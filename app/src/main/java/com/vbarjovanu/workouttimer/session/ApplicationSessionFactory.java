package com.vbarjovanu.workouttimer.session;

import android.content.Context;

import com.vbarjovanu.workouttimer.preferences.WorkoutTimerPreferences;

/**
 * Factory class to get an instance of the IApplicationSession implementation
 */
public class ApplicationSessionFactory {
    private ApplicationSessionFactory() {
    }

    public static IApplicationSession getApplicationSession(Context context) {
        return new ApplicationSession(new WorkoutTimerPreferences(context));
    }
}
