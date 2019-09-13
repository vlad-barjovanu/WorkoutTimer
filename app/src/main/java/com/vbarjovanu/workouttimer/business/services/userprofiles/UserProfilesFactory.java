package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;

public class UserProfilesFactory {
    public static IUserProfilesService getUserProfilesService(IFileRepositorySettings fileRepositorySettings) {
        IUserProfilesFileRepository userProfilesFileRepository;
        userProfilesFileRepository = UserProfilesFactory.getUserProfilesFileRepository();
        return new UserProfilesService(userProfilesFileRepository, fileRepositorySettings, UserProfile.class, UserProfilesList.class);
    }

    public static IUserProfilesFileRepository getUserProfilesFileRepository(){
        return new UserProfilesFileRepository(UserProfile.class, UserProfile[].class);
    }
}
