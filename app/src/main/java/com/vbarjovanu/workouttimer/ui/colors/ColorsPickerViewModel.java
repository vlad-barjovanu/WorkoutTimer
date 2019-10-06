package com.vbarjovanu.workouttimer.ui.colors;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ColorsPickerViewModel extends IColorsPickerViewModel {
    private MutableLiveData<Integer[]> colors;
    private MutableLiveData<Integer> selectedColor;

    public ColorsPickerViewModel() {
        super();
        this.colors = new MutableLiveData<>();
        this.selectedColor = new MutableLiveData<>();
    }

    @Override
    public void loadColors(int[] possibleColors) {
        this.colors.setValue(this.toIntegerArray(possibleColors));
    }

    @Override
    @NonNull
    public LiveData<Integer[]> getColors() {
        return this.colors;
    }

    @Override
    public void selectColor(Integer color) {
        this.selectedColor.setValue(color);
    }

    @Override
    @NonNull
    public LiveData<Integer> getSelectedColor() {
        return this.selectedColor;
    }

    @Override
    public int getColorPosition(Integer color) {
        int position = -1;
        if (this.colors.getValue() != null) {
            for (int i = 0; i < this.colors.getValue().length; i++) {
                if (this.colors.getValue()[i].equals(color)) {
                    position = i;
                    break;
                }
            }
        }
        return position;
    }

    private Integer[] toIntegerArray(int[] possibleColors) {
        Integer[] colors;
        if (possibleColors != null) {
            colors = new Integer[possibleColors.length];
            int i = 0;
            for (int color : possibleColors) {
                colors[i++] = color;
            }
        } else {
            colors = new Integer[0];
        }
        return colors;
    }

}
