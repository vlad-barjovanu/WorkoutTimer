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
     *
     * @param userProfileId ID of the user profile to load for editing
     * @param savedStateUserProfile previous saved state of user profile. may be null
     */
    abstract void loadUserProfile(String userProfileId, UserProfile savedStateUserProfile);

    /**
     * Creates a new user profile to be edited
     *
     * @param savedStateUserProfile previous saved state of user profile. may be null
     */
    abstract void newUserProfile(UserProfile savedStateUserProfile);

    /**
     * Returns true if the view model has been initialised: one of the methods loadUserProfile or newUserProfile was called to initialise the model
     *
     * @return true if initialisation was done, false otherwise
     */
    abstract boolean isInitialised();

    /**
     * Returns the user profile to edit
     *
     * @return user profile live data
     */
    abstract LiveData<UserProfileModel> getUserProfileModel();

    /**
     * Saves the user profile changes
     *
     * @param userProfileModelToSave user profile to be saved
     */
    abstract void saveUserProfile(UserProfileModel userProfileModelToSave);

    /**
     * Cancels the edit of the user profile
     */
    abstract void cancelUserProfileEdit();

    /**
     * returns the action live data of the user profile edit
     *
     * @return the action
     */
    abstract SingleLiveEvent<UserProfileEditFragmentAction> getAction();

    /**
     * Removes the recorded user image and the default one will be restored
     */
    public abstract void deleteUserImage();
}
