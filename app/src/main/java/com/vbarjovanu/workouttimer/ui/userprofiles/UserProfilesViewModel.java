package com.vbarjovanu.workouttimer.ui.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;

public class UserProfilesViewModel extends IUserProfilesViewModel {

    private UserProfilesLiveData userProfilesLiveData;

    private String selectedUserProfileId;

    public UserProfilesViewModel(IFileRepositorySettings fileRepositorySettings) {
        this.userProfilesLiveData = new UserProfilesLiveData(fileRepositorySettings);
    }

    @Override
    public void loadUserProfiles() {
        this.userProfilesLiveData.loadUserProfiles();
    }

    @Override
    public UserProfilesLiveData getUserProfiles() {
        return this.userProfilesLiveData;
    }

    @Override
    public boolean setSelectedUserProfileId(String id) {
        UserProfile userProfile = null;
        if (this.userProfilesLiveData.getValue() != null) {
            userProfile = this.userProfilesLiveData.getValue().find(id);
        }
        if (userProfile != null) {
            this.selectedUserProfileId = id;
            return true;
        }
        return false;
    }

    @Override
    public String getSelectedUserProfileId() {
        return this.selectedUserProfileId;
    }
}