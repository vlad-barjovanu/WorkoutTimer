package com.vbarjovanu.workouttimer.ui.generic.recyclerview;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.vbarjovanu.workouttimer.BR;

public class ItemModel extends BaseObservable {
    private String id;
    private String title;
    private String description;
    private int color;
    private int textColor;
    private OnItemClickListener onItemClickListener;

    public ItemModel(String id, String title, String description, int color, int textColor) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.color = color;
        this.textColor = textColor;
        this.onItemClickListener = null;
    }

    @Bindable
    public String getId() {
        return id;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    /**
     * The background item color
     *
     * @return color
     */
    @Bindable
    public int getColor() {
        return color;
    }

    /**
     * The text item color
     *
     * @return color
     */
    @Bindable
    public int getTextColor() {
        return textColor;
    }

    public ItemModel setColor(int color) {
        this.color = color;
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.color);
        return this;
    }

    public ItemModel setTextColor(int textColor) {
        this.textColor = textColor;
        this.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.textColor);
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void onClick(ItemModel model, View view) {
        if (this.onItemClickListener != null) {
            this.onItemClickListener.onClick(model, view);
        }
    }
}
