package com.vbarjovanu.workouttimer.business.services.generic;

public class FileRepositorySettings implements IFileRepositorySettings {
    private String folderPath;

    public FileRepositorySettings(String folderPath) {
        this.setFolderPath(folderPath);
    }

    @Override
    public String getFolderPath() {
        return this.folderPath;
    }

    private void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }
}
