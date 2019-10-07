package com.vbarjovanu.workouttimer.ui.userprofiles.list;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.userprofiles.IUserProfilesService;
import com.vbarjovanu.workouttimer.session.IApplicationSession;
import com.vbarjovanu.workouttimer.ui.generic.events.SingleLiveEvent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.ISynchronizable;

public abstract class IUserProfilesViewModel extends ViewModel implements ISynchronizable{

    IUserProfilesViewModel() {
        super();
    }

    /**
     * Loads the user profiles
     */
    abstract void loadUserProfiles();

    /**
     * Rerturns the loaded user profiles
     * @return user profiles live data
     */
    abstract LiveData<UserProfilesList> getUserProfiles();

    /**
     * Selects a specific user profile by ID
     * @param id user profile's ID that was selected by user
     * @return true if selection is successful
     */
    abstract boolean setSelectedUserProfileId(String id);

    /**
     * Returns the selected user profile ID
     * @return selected user profile's ID
     */
    abstract String getSelectedUserProfileId();

    /**
     *
     * @return data of the user profile action
     */
    abstract SingleLiveEvent<UserProfilesFragmentActionData> getActionData();

    /**
     * Starts editing the user profile
     * @param id of the user profile to edit
     * @return true if user profile can be edited
     */
    abstract boolean editUserProfile(String id);

    /**
     * Starts editing a new user profile
     * @return true if a new user profile can be edited
     */
    abstract boolean newUserProfile();
}
