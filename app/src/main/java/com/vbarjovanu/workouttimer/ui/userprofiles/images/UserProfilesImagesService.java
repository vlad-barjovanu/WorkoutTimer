package com.vbarjovanu.workouttimer.ui.userprofiles.images;

import android.content.Context;
import android.graphics.Bitmap;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.helpers.images.BitmapHelper;

public class UserProfilesImagesService implements IUserProfilesImagesService {
    private Context context;
    private Bitmap defaultUserImage;

    public UserProfilesImagesService(Context context) {
        this.context = context;
    }

    @Override
    public Bitmap getUserImage(UserProfile userProfile) {
        Bitmap userImage = null;
        if (userProfile != null) {
            if (userProfile.getImageFilePath() != null) {
                userImage = BitmapHelper.fromFilePath(userProfile.getImageFilePath());
            }
            userImage = (userImage == null) ? this.getDefaultUserImage() : userImage;
        }

        return userImage;
    }

    private Bitmap getDefaultUserImage() {
        if (this.defaultUserImage == null) {
            this.defaultUserImage = BitmapHelper.fromResource(this.context, R.drawable.userprofile);
        }
        return this.defaultUserImage;
    }
}
