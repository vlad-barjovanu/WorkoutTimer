package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class UserProfilesFileRepositoryTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void loadUserProfilesFile() {
        IUserProfilesFileRepository userProfilesFileRepository = new UserProfilesFileRepository(UserProfile.class, UserProfile[].class);
        UserProfile[] userProfiles = null;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            assert classLoader != null;
            URL resource = classLoader.getResource("business/services/userprofiles/UserProfiles.json");
            userProfiles = userProfilesFileRepository.loadModelsFromFile(resource.getPath());
        } catch (IOException e) {
            Assert.assertNull(e);
        }
        Assert.assertNotNull(userProfiles);
        Assert.assertEquals(2, userProfiles.length);
        Assert.assertEquals("abc", userProfiles[0].getId());
        Assert.assertEquals("john", userProfiles[0].getName());
        Assert.assertEquals("def", userProfiles[1].getId());
        Assert.assertEquals("doe", userProfiles[1].getName());
    }

    @Test
    public void saveWorkoutsFile() {
        IUserProfilesFileRepository userProfilesFileRepository = new UserProfilesFileRepository(UserProfile.class, UserProfile[].class);
        String filePath = null;
        UserProfile[] userProfilesLoaded = null;

        UserProfile[] userProfiles = new UserProfile[2];
        userProfiles[0] = new UserProfile("abc");
        userProfiles[0].setName("userProfile1");
        userProfiles[1] = new UserProfile("def");
        userProfiles[1].setName("userProfile2");

        try {
            File tempFile = folder.newFile("file.txt");
            filePath = tempFile.getPath();
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }
        try {
            userProfilesFileRepository.saveModelsToFile(filePath, userProfiles);
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }

        try {
            userProfilesLoaded = userProfilesFileRepository.loadModelsFromFile(filePath);
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }
        Assert.assertNotNull(userProfilesLoaded);
        Assert.assertEquals(2, userProfilesLoaded.length);
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(userProfiles[i].getId(), userProfilesLoaded[i].getId());
            Assert.assertEquals(userProfiles[i].getName(), userProfilesLoaded[i].getName());
        }
    }
}