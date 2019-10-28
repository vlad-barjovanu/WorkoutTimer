package com.vbarjovanu.workouttimer.helpers.converters;

import android.content.Context;

import androidx.databinding.InverseMethod;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.ui.workouts.training.models.WorkoutTrainingItemType;

public class WorkoutTrainingItemTypeStringConverter {
    private static int[] ids = new int[]{R.string.prepare, R.string.work, R.string.rest, R.string.rest_between_sets, R.string.cool_down};
    private static WorkoutTrainingItemType[] types = new WorkoutTrainingItemType[]{WorkoutTrainingItemType.PREPARE, WorkoutTrainingItemType.WORK, WorkoutTrainingItemType.REST, WorkoutTrainingItemType.SET_REST, WorkoutTrainingItemType.COOL_DOWN};

    @InverseMethod("stringToWorkoutTrainingItemType")
    public static String WorkoutTrainingItemTypeToString(Context context, WorkoutTrainingItemType value) {
        String text = "";
        if (value != null) {
            switch (value) {
                case PREPARE:
                    text = context.getString(R.string.prepare);
                    break;
                case WORK:
                    text = context.getString(R.string.work);
                    break;
                case REST:
                    text = context.getString(R.string.rest);
                    break;
                case SET_REST:
                    text = context.getString(R.string.rest_between_sets);
                    break;
                case COOL_DOWN:
                    text = context.getString(R.string.cool_down);
                    break;
            }
        }
        return text;
    }

    public static WorkoutTrainingItemType stringToWorkoutTrainingItemType(Context context, String value) {
        String text;
        WorkoutTrainingItemType type = null;
        if (value != null && !value.isEmpty()) {
            for (int i = 0; i < ids.length; i++) {
                text = context.getString(ids[i]);
                if (text.equals(value)) {
                    type = types[i];
                    break;
                }
            }
        }
        return type;
    }
}
