package com.vbarjovanu.workouttimer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

abstract class BasePreferences implements IBasePreferences {
    @NonNull
    final SharedPreferences sharedPreferences;
    @NonNull
    protected final Context context;

    BasePreferences(@NonNull Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(this.getPreferencesName(), Context.MODE_PRIVATE);
    }

    /**
     * Can be overridden in children classes to return custom name
     *
     * @return The preferences name. Default is simple class name.
     */
    @SuppressWarnings("WeakerAccess")
    protected String getPreferencesName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void clear() {
        this.sharedPreferences.edit().clear().apply();
    }
}
