package com.vbarjovanu.workouttimer.ui.userprofiles;

import androidx.lifecycle.ViewModel;

public abstract class IUserProfilesViewModel extends ViewModel {
    /**
     * Loads the user profiles
     */
    public abstract void loadUserProfiles();

    /**
     * Rerturns the loaded user profiles
     * @return user profiles live data
     */
    public abstract UserProfilesLiveData getUserProfiles();

    /**
     * Selects a specific user profile by ID
     * @param id user profile's ID that was selected by user
     * @return true if selection is successful
     */
    public abstract boolean setSelectedUserProfileId(String id);

    /**
     * Returns the selected user profile ID
     * @return selected user profile's ID
     */
    public abstract String getSelectedUserProfileId();
}
