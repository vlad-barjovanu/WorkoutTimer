package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.graphics.Bitmap;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;

public class UserProfileModel extends BaseObservable {
    private final UserProfile userProfile;
    private Bitmap userImage;

    UserProfileModel(UserProfile userProfile, Bitmap userImage) {
        this.userProfile = userProfile;
        this.userImage = userImage;
    }

    @Bindable
    public UserProfile getUserProfile() {
        return userProfile;
    }

    @Bindable
    public Bitmap getUserImage() {
        return userImage;
    }

    public UserProfileModel setUserImage(Bitmap userImage) {
        this.userImage = userImage;
        return this;
    }
}
