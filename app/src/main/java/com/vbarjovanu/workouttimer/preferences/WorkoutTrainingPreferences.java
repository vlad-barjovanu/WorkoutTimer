package com.vbarjovanu.workouttimer.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemType;

public class WorkoutTrainingPreferences extends BasePreferences implements IWorkoutTrainingPreferences {
    private static final String KeyIncreaseDuration = "IncreaseDuration";

    WorkoutTrainingPreferences(Context context) {
        super(context);
    }

    @Override
    public int getColor(WorkoutTrainingItemType type) {
        return this.sharedPreferences.getInt(this.getColorKey(type), this.getDefaultColor(type));
    }

    private int getDefaultColor(WorkoutTrainingItemType type) {
        int color;
        switch (type) {
            case REST:
                color = Color.parseColor("#ff33b5e5");
                break;
            case COOL_DOWN:
                color = Color.parseColor("#ff99cc00");
                break;
            case PREPARE:
                color = Color.parseColor("#ffffbb33");
                break;
            case WORK:
                color = Color.parseColor("#ffff4444");
                break;
            case SET_REST:
                color = Color.parseColor("#ffaa66cc");
                break;
            default:
                color = Color.parseColor("#ff0099cc");
        }
        return color;
    }

    @Override
    public void setColor(WorkoutTrainingItemType type, int color) {
        this.sharedPreferences.edit().putInt(this.getColorKey(type), color).apply();
    }

    private String getColorKey(WorkoutTrainingItemType type) {
        return "Color_" + type.toString();
    }

    @Override
    public boolean isIncreaseDuration() {
        return this.sharedPreferences.getBoolean(KeyIncreaseDuration, false);
    }

    @Override
    public void setIncreaseDuration(boolean increased) {
        this.sharedPreferences.edit().putBoolean(KeyIncreaseDuration, increased).apply();
    }
}
