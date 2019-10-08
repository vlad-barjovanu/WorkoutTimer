package com.vbarjovanu.workouttimer.ui.userprofiles.images;

import android.graphics.Bitmap;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;

public interface IUserProfilesImagesService {
    /**
     * Returns a bitmap from the userProfile imageFilePath property
     * @param userProfile user's profile object
     * @return Bitmap object
     */
    Bitmap getUserImage(UserProfile userProfile);

    /**
     * Stores a user image and updates the userProfile imageFilePath property
     * @param userProfile user's profile object
     * @param userImage user's image as Bitmap object
     */
    void setUserImage(UserProfile userProfile, Bitmap userImage);
}
