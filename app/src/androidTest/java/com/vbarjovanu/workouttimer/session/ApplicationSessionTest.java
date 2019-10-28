package com.vbarjovanu.workouttimer.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.preferences.IWorkoutTimerPreferences;
import com.vbarjovanu.workouttimer.preferences.WorkoutTimerPreferences;

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
        IWorkoutTimerPreferences preferences = new WorkoutTimerPreferences(context);
        preferences.clear();
        this.applicationSession = new ApplicationSession(preferences);
    }

    @Test
    public void setFileRepositoriesFolderPath() throws FileNotFoundException {
        this.applicationSession.getWorkoutTimerPreferences().getFileRepositoryPreferences().setFolderPath(this.folder.getRoot().getAbsolutePath());
        Assert.assertTrue(true);
    }

    @Test(expected = FileNotFoundException.class)
    public void setFileRepositoriesFolderPathInvalid() throws FileNotFoundException {
        this.applicationSession.getWorkoutTimerPreferences().getFileRepositoryPreferences().setFolderPath("dingo");
    }

    @Test
    public void getFileRepositoriesFolderPath() {
        Assert.assertNotNull(this.applicationSession.getWorkoutTimerPreferences().getFileRepositoryPreferences().getFolderPath());
        Assert.assertEquals(this.context.getDir("FileRepositories", Context.MODE_APPEND).getAbsolutePath(), this.applicationSession.getWorkoutTimerPreferences().getFileRepositoryPreferences().getFolderPath());
    }

    @Test
    public void getFileRepositoriesFolderPathValid() throws FileNotFoundException {
        String folderPath;
        folderPath = this.folder.getRoot().getAbsolutePath();
        this.applicationSession.getWorkoutTimerPreferences().getFileRepositoryPreferences().setFolderPath(folderPath);
        Assert.assertNotNull(this.applicationSession.getWorkoutTimerPreferences().getFileRepositoryPreferences().getFolderPath());
        Assert.assertEquals(folderPath, this.applicationSession.getWorkoutTimerPreferences().getFileRepositoryPreferences().getFolderPath());
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