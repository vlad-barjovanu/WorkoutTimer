package com.vbarjovanu.workouttimer.preferences;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;

public class FileRepositoryPreferences extends BasePreferences implements IFileRepositoryPreferences {
    private static final String KeyFolderPath = "FolderPath";

    FileRepositoryPreferences(Context context) {
        super(context);
    }

    @Override
    public String getFolderPath() {
        return this.sharedPreferences.getString(KeyFolderPath, this.getDefaultFolderPath());
    }

    private String getDefaultFolderPath() {
        String folderPath = null;
        File file = this.context.getDir("FileRepositories", Context.MODE_PRIVATE);
        if (file != null) {
            folderPath = file.getAbsolutePath();
        }
        return folderPath;
    }

    @Override
    public void setFolderPath(String folderPath) throws FileNotFoundException {
        File file = new File(folderPath);
        if (!file.exists() || !file.isDirectory()) {
            throw new FileNotFoundException(folderPath);
        }
        this.sharedPreferences.edit().putString(KeyFolderPath, folderPath).apply();
    }
}
