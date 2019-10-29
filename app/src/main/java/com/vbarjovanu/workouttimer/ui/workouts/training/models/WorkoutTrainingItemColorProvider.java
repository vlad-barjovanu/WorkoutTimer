package com.vbarjovanu.workouttimer.ui.workouts.training.models;

import android.graphics.Color;

import androidx.annotation.NonNull;

import com.vbarjovanu.workouttimer.preferences.IWorkoutTrainingPreferences;

import java.util.HashMap;

public class WorkoutTrainingItemColorProvider implements IWorkoutTrainingItemColorProvider {
    @NonNull
    private final HashMap<WorkoutTrainingItemType, Integer> workoutTrainingItemsColors;

    public WorkoutTrainingItemColorProvider(@NonNull IWorkoutTrainingPreferences workoutTrainingPreferences) {
        this.workoutTrainingItemsColors = this.buildColors(workoutTrainingPreferences);
    }

    /**
     * Creates a hashmap between WorkoutTrainingItemType and color, based on the preferences
     *
     * @param workoutTrainingPreferences preferences
     * @return hashmap
     */
    private HashMap<WorkoutTrainingItemType, Integer> buildColors(@NonNull IWorkoutTrainingPreferences workoutTrainingPreferences) {

        HashMap<WorkoutTrainingItemType, Integer> workoutTrainingItemsColors;
        workoutTrainingItemsColors = new HashMap<>();
        for (WorkoutTrainingItemType itemType : WorkoutTrainingItemType.values()) {
            workoutTrainingItemsColors.put(itemType, workoutTrainingPreferences.getColor(itemType));
        }

        return workoutTrainingItemsColors;
    }

    @Override
    public int getColor(WorkoutTrainingItemType itemType) {
        int color;
        if (this.workoutTrainingItemsColors.containsKey(itemType) && this.workoutTrainingItemsColors.get(itemType) != null) {
            //noinspection ConstantConditions
            color = this.workoutTrainingItemsColors.get(itemType);
        } else {
            color = Color.parseColor("#ff33b5e5");
        }
        return color;
    }
}
