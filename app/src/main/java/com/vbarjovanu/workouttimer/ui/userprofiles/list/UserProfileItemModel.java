package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.graphics.Bitmap;

import androidx.databinding.Bindable;

import com.vbarjovanu.workouttimer.ui.generic.recyclerview.ItemModel;

public class UserProfileItemModel extends ItemModel {
    private Bitmap userImage;

    UserProfileItemModel(String id, String title, String description, int color, Bitmap userImage) {
        super(id, title, description, color);
        this.userImage = userImage;
    }

    @Bindable
    public Bitmap getUserImage() {
        return userImage;
    }
}
