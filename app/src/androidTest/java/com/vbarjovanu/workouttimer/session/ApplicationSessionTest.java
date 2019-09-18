package com.vbarjovanu.workouttimer.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileNotFoundException;

public class ApplicationSessionTest {

    private IApplicationSession applicationSession;

    private Context context;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() {
        // use the App context (getTargetContext) - otherwise repo folder can't be created
        this.context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        // clear all prev stored preferences, so unit tests are run clean
        sharedPreferences.edit().clear().commit();
        this.applicationSession = new ApplicationSession(this.context);
    }

    @Test
    public void setFileRepositoriesFolderPath() throws FileNotFoundException {
        this.applicationSession.setFileRepositoriesFolderPath(this.folder.getRoot().getAbsolutePath());
        Assert.assertTrue(true);
    }

    @Test(expected = FileNotFoundException.class)
    public void setFileRepositoriesFolderPathInvalid() throws FileNotFoundException {
        this.applicationSession.setFileRepositoriesFolderPath("dingo");
    }

    @Test
    public void getFileRepositoriesFolderPath() {
        Assert.assertNotNull(this.applicationSession.getFileRepositoriesFolderPath());
        Assert.assertEquals(this.context.getDir("FileRepositories", Context.MODE_APPEND).getAbsolutePath(), this.applicationSession.getFileRepositoriesFolderPath());
    }

    @Test
    public void getFileRepositoriesFolderPathValid() throws FileNotFoundException {
        String folderPath;
        folderPath = this.folder.getRoot().getAbsolutePath();
        this.applicationSession.setFileRepositoriesFolderPath(folderPath);
        Assert.assertNotNull(this.applicationSession.getFileRepositoriesFolderPath());
        Assert.assertEquals(folderPath, this.applicationSession.getFileRepositoriesFolderPath());
    }

    @Test
    public void setUserProfileId() {
        this.applicationSession.setUserProfileId("123");
        Assert.assertTrue(true);
    }

    @Test
    public void getUserProfileId() {
        String profileId;
        profileId = "123";
        this.applicationSession.setUserProfileId(profileId);
        Assert.assertEquals(profileId, this.applicationSession.getUserProfileId());
    }
}