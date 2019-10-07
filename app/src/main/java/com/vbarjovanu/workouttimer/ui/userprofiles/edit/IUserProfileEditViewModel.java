package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

public abstract class IUserProfileEditViewModel extends ViewModel implements ISynchronizable {
    IUserProfileEditViewModel() {
        super();
    }

    /**
     * Loads the user profile to be edited
     * @param userProfileId ID of the user profile to load for editing
     */
    abstract void loadUserProfile(String userProfileId);

    /**
     * Creates a new user profile to be edited
     */
    abstract void newUserProfile();

    /**
     * Returns the user profile to edit
     * @return user profile live data
     */
    abstract LiveData<UserProfile> getUserProfile();

    /**
     * Saves the user profile changes
     * @param name name of the user profile
     * @param description description of the user profile
     * @param newImageBitmap new image bitmap
     */
    abstract void saveUserProfile(String name, String description, Bitmap newImageBitmap);

    /**
     * Cancels the edit of the user profile
     */
    abstract void cancelUserProfileEdit();

    /**
     * returns the action live data of the user profile edit
     * @return the action
     */
    abstract SingleLiveEvent<UserProfileEditFragmentAction> getAction();
}
