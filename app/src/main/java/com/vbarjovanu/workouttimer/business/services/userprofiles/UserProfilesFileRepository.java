package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.generic.ModelsFileRepository;

public class UserProfilesFileRepository extends ModelsFileRepository<UserProfile> implements IUserProfilesFileRepository  {
    /**
     * @param classT
     * @param classArrayOfT
     */
    public UserProfilesFileRepository(Class<UserProfile> classT, Class<UserProfile[]> classArrayOfT) {
        super(classT, classArrayOfT);
    }
}
