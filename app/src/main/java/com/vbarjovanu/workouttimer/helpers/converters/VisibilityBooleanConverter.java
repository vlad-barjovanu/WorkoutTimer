package com.vbarjovanu.workouttimer.helpers.converters;

import android.view.View;

import androidx.databinding.InverseMethod;

public class VisibilityBooleanConverter {
    @InverseMethod("visibilityToBoolean")
    public static int booleanToVisibility(boolean value) {
        if (value) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    public static boolean visibilityToBoolean(int value) {
        return (value == View.VISIBLE);
    }
}
