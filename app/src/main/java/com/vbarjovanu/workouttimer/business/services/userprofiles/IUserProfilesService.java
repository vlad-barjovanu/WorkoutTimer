package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IModelsService;

import java.lang.reflect.InvocationTargetException;

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

    /**
     * Deletes a user profile identified by the ID
     * @param userProfileId ID of the user profile to delete
     * @return true if the model was deleted successfully
     */
    boolean deleteModel(String userProfileId);

    /**
     * Deletes the user profile
     * @param userProfile user profile to delete
     * @return true if the model was deleted successfully
     */
    boolean deleteModel(UserProfile userProfile);

    /**
     * Creates a default user profile
     * @return user profile
     * @throws IllegalAccessException can't call constructor for new model
     * @throws InvocationTargetException constructor call for new model fails
     * @throws InstantiationException constructor call for new model fails
     */
    UserProfile createDefaultModel() throws IllegalAccessException, InvocationTargetException, InstantiationException;

    /**
     * Returns the folder path where user profile images are stored
     * @return folder path
     */
    String getImagesFolderPath();
}
