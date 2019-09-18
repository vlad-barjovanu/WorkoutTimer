package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.workouts.WorkoutsFileRepository;
import com.vbarjovanu.workouttimer.business.services.workouts.WorkoutsService;
import com.vbarjovanu.workouttimer.helpers.files.TextFileReader;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class UserProfilesServiceTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void loadUserProfiles() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        assert classLoader != null;
        URL resource = classLoader.getResource("business/services/userprofiles/UserProfiles.json");
        List<UserProfile> userProfileList;
        String folderPath = resource.getPath().replace("/UserProfiles.json", "");
        IUserProfilesFileRepository userProfilesFileRepository = new UserProfilesFileRepository(UserProfile.class, UserProfile[].class);
        IFileRepositorySettings settings = new FileRepositorySettings(folderPath);
        IUserProfilesService userProfilesService = new UserProfilesService(userProfilesFileRepository, settings, UserProfile.class, UserProfilesList.class);
        userProfileList = userProfilesService.loadModels();
        Assert.assertNotNull(userProfileList);
        Assert.assertEquals(2, userProfileList.size());
        Assert.assertEquals("abc", userProfileList.get(0).getId());
        Assert.assertEquals("john", userProfileList.get(0).getName());
        Assert.assertEquals("def", userProfileList.get(1).getId());
        Assert.assertEquals("doe", userProfileList.get(1).getName());
    }

    @Test
    public void loadUserProfile() {
        UserProfile userProfile;
        ClassLoader classLoader = this.getClass().getClassLoader();
        assert classLoader != null;
        URL resource = classLoader.getResource("business/services/userprofiles/UserProfiles.json");
        String folderPath = resource.getPath().replace("/UserProfiles.json", "");
        IUserProfilesFileRepository userProfilesFileRepository = new UserProfilesFileRepository(UserProfile.class, UserProfile[].class);
        IFileRepositorySettings settings = new FileRepositorySettings(folderPath);
        IUserProfilesService userProfilesService = new UserProfilesService(userProfilesFileRepository, settings, UserProfile.class, UserProfilesList.class);
        String userProfileId = "def";
        userProfile = userProfilesService.loadModel(userProfileId);
        Assert.assertNotNull(userProfile);
        Assert.assertEquals("def", userProfile.getId());
        Assert.assertEquals("doe", userProfile.getName());
    }

    @Test
    public void saveUserProfile() throws IOException {
        String profileId;
        UserProfile userProfile;
        String folderPath;
        folderPath = folder.getRoot().getPath();
        IFileRepositorySettings settings = new FileRepositorySettings(folderPath);
        IUserProfilesFileRepository userProfilesFileRepository = new UserProfilesFileRepository(UserProfile.class, UserProfile[].class);
        IUserProfilesService userProfilesService = new UserProfilesService(userProfilesFileRepository, settings, UserProfile.class, UserProfilesList.class);

        userProfile = new UserProfile("ghi");
        userProfile.setName("profile3");
        userProfilesService.saveModel(userProfile);
        File file = new File(folderPath+"/UserProfiles.json");
        Assert.assertTrue(file.exists());
        TextFileReader textFileReader = new TextFileReader();
        StringBuilder sb = textFileReader.readFile(file.getPath());
        Assert.assertTrue(sb.toString().contains("ghi"));
    }

    @Test
    public void createUserProfile() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        UserProfile userProfile;
        String folderPath;
        folderPath = folder.getRoot().getPath();
        IFileRepositorySettings settings = new FileRepositorySettings(folderPath);
        IUserProfilesFileRepository userProfilesFileRepository = new UserProfilesFileRepository(UserProfile.class, UserProfile[].class);
        IUserProfilesService userProfilesService = new UserProfilesService(userProfilesFileRepository, settings, UserProfile.class, UserProfilesList.class);
        userProfile=userProfilesService.createModel();
        Assert.assertNotNull(userProfile);
        Assert.assertNotNull(userProfile.getId());
    }
}