package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IModelsService;

public interface IUserProfilesService extends IModelsService<UserProfile, UserProfilesList> {

    /**
     * @return a list of user profiles
     */
    UserProfilesList loadModels();

    /**
     * @param primaryKey models's ID
     * @return the user profile identified by the ID
     */
    UserProfile loadModel(String primaryKey);

    /**
     * @param userProfile   user profile to save
     * @return true if the model was saved successfully
     */
    boolean saveModel(UserProfile userProfile);
}
