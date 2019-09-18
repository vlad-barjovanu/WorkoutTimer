package com.vbarjovanu.workouttimer.helpers.files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class FolderHelperTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void mkdirAlreadyExists() {
        String folderPath = folder.getRoot().getAbsolutePath();
        assertTrue(FolderHelper.mkdir(folderPath, false));
    }

    @Test
    public void mkdirDoesntExistButParentExists() {
        String folderPath = folder.getRoot().getAbsolutePath();
        folderPath += "/newfolder";
        assertTrue(FolderHelper.mkdir(folderPath, false));
    }

    @Test
    public void mkdirDoesntExistButParentExistsRecursive() {
        String folderPath = folder.getRoot().getAbsolutePath();
        folderPath += "/newfolder";
        assertTrue(FolderHelper.mkdir(folderPath, true));
    }

    @Test
    public void mkdirDoesntExistAndParentDoesntExist() {
        String folderPath = folder.getRoot().getAbsolutePath();
        folderPath += "/newfolder1/newfolder2";
        assertFalse(FolderHelper.mkdir(folderPath, false));
    }

    @Test
    public void mkdirDoesntExistAndParentDoesntExistRecursive() {
        String folderPath = folder.getRoot().getAbsolutePath();
        folderPath += "/newfolder1/newfolder2";
        assertTrue(FolderHelper.mkdir(folderPath, true));
    }
}