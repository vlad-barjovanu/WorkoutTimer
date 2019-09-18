package com.vbarjovanu.workouttimer.helpers.files;

import java.io.File;

@SuppressWarnings("WeakerAccess")
public class FolderHelper {
    /**
     * Creates a folder. When createRecursive is true it creates the entire folder path if parent folders don't exist
     * @param filePath the absolute folder path to create
     * @param createRecursive true to create entire parent folder path structure
     * @return true if folder was created
     */
    public static boolean mkdir(String filePath, boolean createRecursive) {
        File folder = new File(filePath);

        if (folder.exists()) {
            return true;
        }

        if (createRecursive) {
            if (!FolderHelper.mkdir(folder.getParentFile().getAbsolutePath(), true)) {
                return false;
            }
        }
        return folder.mkdir();
    }
}
