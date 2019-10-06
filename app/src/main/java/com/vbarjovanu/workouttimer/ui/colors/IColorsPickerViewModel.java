package com.vbarjovanu.workouttimer.ui.colors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public abstract class IColorsPickerViewModel extends ViewModel {
    public abstract void loadColors(int[] possibleColors);

    @NonNull
    public abstract LiveData<Integer[]> getColors();

    public abstract void selectColor(Integer color);

    @NonNull
    public abstract LiveData<Integer> getSelectedColor();

    /**
     * Returns the color position within the colors
     * @param color for which to return the position
     * @return position or -1 if color doesn't exist in the collection
     */
    public abstract int getColorPosition(Integer color);
}
