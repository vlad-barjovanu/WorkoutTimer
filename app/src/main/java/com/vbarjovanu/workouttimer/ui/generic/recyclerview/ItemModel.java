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
    private OnItemClickListener onItemClickListener;

    public ItemModel(String id, String title, String description, int color) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.color = color;
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

    @Bindable
    public int getColor() {
        return color;
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
