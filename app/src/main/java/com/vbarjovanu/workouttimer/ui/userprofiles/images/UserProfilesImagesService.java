package com.vbarjovanu.workouttimer.ui.userprofiles.images;

import android.content.Context;
import android.graphics.Bitmap;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.helpers.images.BitmapHelper;

import java.io.IOException;

public class UserProfilesImagesService implements IUserProfilesImagesService {
    private final Context context;
    private final IFileRepositorySettings fileRepositorySettings;
    private Bitmap defaultUserImage;

    public UserProfilesImagesService(Context context, IFileRepositorySettings fileRepositorySettings) {
        this.context = context;
        this.fileRepositorySettings = fileRepositorySettings;
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

    @Override
    public void setUserImage(UserProfile userProfile, Bitmap userImage) {
        String filePath;
        if (userProfile != null) {
            if (userImage == null) {
                filePath = null;
            } else {
                try {
                    filePath = this.getImagesFolderPath() + userProfile.getId() + ".png";
                    BitmapHelper.toFile(userImage, filePath);
                } catch (IOException e) {
                    filePath = null;
                }
            }
            userProfile.setImageFilePath(filePath);
        }
    }

    private Bitmap getDefaultUserImage() {
        if (this.defaultUserImage == null) {
            this.defaultUserImage = BitmapHelper.fromResource(this.context, R.drawable.userprofile);
        }
        return this.defaultUserImage;
    }

    private String getImagesFolderPath() {
        return this.fileRepositorySettings.getFolderPath() + "/images/";
    }
}
