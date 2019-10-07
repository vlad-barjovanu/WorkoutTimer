package com.vbarjovanu.workouttimer.ui.userprofiles.images;

import android.graphics.Bitmap;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;

public interface IUserProfilesImagesService {
    Bitmap getUserImage(UserProfile userProfile);
}
