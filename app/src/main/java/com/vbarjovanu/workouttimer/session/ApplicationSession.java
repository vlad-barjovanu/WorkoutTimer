package com.vbarjovanu.workouttimer.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

class ApplicationSession implements IApplicationSession {

    private Context context;
    private SharedPreferences sharedPreferences;
    private static final String KeyFileRepositoriesFolderPath = "FileRepositoriesFolderPath";
    private static final String KeyUserProfileId = "UserProfileId";

    ApplicationSession(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void setFileRepositoriesFolderPath(String folderPath) throws FileNotFoundException {
        File file = new File(folderPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new FileNotFoundException(folderPath);
        }

        this.sharedPreferences.edit().putString(KeyFileRepositoriesFolderPath, folderPath).apply();
    }

    @Override
    public String getFileRepositoriesFolderPath() {
        String folderPath = this.sharedPreferences.getString(KeyFileRepositoriesFolderPath, null);
        if (folderPath == null) {
            File file = this.context.getDir("FileRepositories", Context.MODE_PRIVATE);
            if (file != null) {
                folderPath = file.getAbsolutePath();
                try {
                    this.setFileRepositoriesFolderPath(folderPath);
                } catch (FileNotFoundException e) {
                    folderPath = null;
                }
            }
        }
        return folderPath;
    }

    @Override
    public void setUserProfileId(String id) {
        this.sharedPreferences.edit().putString(KeyUserProfileId, id).apply();
    }

    @Override
    public String getUserProfileId() {
        return this.sharedPreferences.getString(KeyUserProfileId, null);
    }
}
