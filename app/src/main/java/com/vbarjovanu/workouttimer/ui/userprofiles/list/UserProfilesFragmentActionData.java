package com.vbarjovanu.workouttimer.ui.userprofiles.list;

public class UserProfilesFragmentActionData {
    private final UserProfilesFragmentAction action;
    private final String userProfileId;

    UserProfilesFragmentActionData(UserProfilesFragmentAction action) {
        this.action = action;
        this.userProfileId = null;
    }

    UserProfilesFragmentActionData(UserProfilesFragmentAction action, String userProfileId) {
        this.action = action;
        this.userProfileId = userProfileId;
    }

    public UserProfilesFragmentAction getAction() {
        return action;
    }

    String getUserProfileId() {
        return userProfileId;
    }
}
