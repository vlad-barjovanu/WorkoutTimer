package com.vbarjovanu.workouttimer.session;

import java.io.FileNotFoundException;

public interface IApplicationSession {
    /**
     * Sets the folder path of the file repositories
     * @param folderPath file repositories folder path
     */
    void setFileRepositoriesFolderPath(String folderPath) throws FileNotFoundException;

    /**
     * Returns the folder path of the file repositories
     * @return folder path
     */
    String getFileRepositoriesFolderPath();

    /**
     * Sets the user profile's ID
     * @param id user profile's ID
     */
    void setUserProfileId(String id);

    /**
     * Returns the user profile ID
     * @return user profile's ID
     */
    String getUserProfileId();
}
