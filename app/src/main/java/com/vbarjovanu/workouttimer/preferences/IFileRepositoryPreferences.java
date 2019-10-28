package com.vbarjovanu.workouttimer.preferences;

import java.io.FileNotFoundException;

public interface IFileRepositoryPreferences extends IBasePreferences {
    String getFolderPath();

    void setFolderPath(String folderPath) throws FileNotFoundException;
}
