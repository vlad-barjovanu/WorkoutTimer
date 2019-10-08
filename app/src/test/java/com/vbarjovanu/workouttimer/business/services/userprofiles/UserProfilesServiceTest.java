package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfilesList;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserProfilesServiceTest {

    @Mock
    private IFileRepositorySettings settings;
    @Mock
    private IUserProfilesFileRepository userProfilesFileRepository;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private IUserProfilesService userProfilesService;

    @Before
    public void setup() throws IOException {
        UserProfile[] userProfiles;
        userProfiles = new UserProfile[2];
        userProfiles[0] = new UserProfile("abc").setName("john");
        userProfiles[1] = new UserProfile("def").setName("doe");

        this.settings = mock(IFileRepositorySettings.class);
        Mockito.when(this.settings.getFolderPath()).thenReturn(this.folder.getRoot().getAbsolutePath());
        this.userProfilesFileRepository = mock(IUserProfilesFileRepository.class);
        Mockito.when(this.userProfilesFileRepository.loadModelsFromFile(any(String.class))).thenReturn(userProfiles);
        this.userProfilesService = new UserProfilesService(userProfilesFileRepository, settings, UserProfile.class, UserProfilesList.class);
    }

    @Test
    public void loadUserProfiles() {
        UserProfilesList userProfileList;
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
        String userProfileId = "def";
        userProfile = this.userProfilesService.loadModel(userProfileId);
        Assert.assertNotNull(userProfile);
        Assert.assertEquals("def", userProfile.getId());
        Assert.assertEquals("doe", userProfile.getName());
    }

    @Test
    public void saveUserProfile() throws IOException {
        UserProfile userProfile;

        userProfile = new UserProfile("ghi");
        userProfile.setName("profile3");
        Assert.assertTrue(userProfilesService.saveModel(userProfile));
        verify(this.userProfilesFileRepository, times(1)).loadModelsFromFile(any(String.class));
        ArgumentCaptor<UserProfile[]> argument = ArgumentCaptor.forClass(UserProfile[].class);
        verify(this.userProfilesFileRepository, times(1)).saveModelsToFile(any(String.class), argument.capture());
        assertNotNull(argument.getValue());
        assertEquals(3, argument.getValue().length);
        assertEquals("ghi", argument.getValue()[2].getId());
    }

    @Test
    public void deleteUserProfile() throws IOException {
        UserProfile userProfile;
        userProfile = userProfilesService.loadModel("def");
        Assert.assertNotNull(userProfile);

        Assert.assertTrue(userProfilesService.deleteModel(userProfile));
        verify(this.userProfilesFileRepository, times(2)).loadModelsFromFile(any(String.class));
        ArgumentCaptor<UserProfile[]> argument = ArgumentCaptor.forClass(UserProfile[].class);
        verify(this.userProfilesFileRepository, times(1)).saveModelsToFile(any(String.class), argument.capture());
        assertNotNull(argument.getValue());
        assertEquals(1, argument.getValue().length);
        assertEquals("abc", argument.getValue()[0].getId());
    }

    @Test
    public void deleteUnknownUserProfile() throws IOException {
        UserProfile userProfile;
        userProfile = new UserProfile("ghi");

        Assert.assertFalse(userProfilesService.deleteModel(userProfile));
        verify(this.userProfilesFileRepository, times(1)).loadModelsFromFile(any(String.class));
        ArgumentCaptor<UserProfile[]> argument = ArgumentCaptor.forClass(UserProfile[].class);
        verify(this.userProfilesFileRepository, times(0)).saveModelsToFile(any(String.class), any(UserProfile[].class));
    }

    @Test
    public void deleteUserProfileByPk() throws IOException {
        Assert.assertTrue(userProfilesService.deleteModel("def"));
        verify(this.userProfilesFileRepository, times(1)).loadModelsFromFile(any(String.class));
        ArgumentCaptor<UserProfile[]> argument = ArgumentCaptor.forClass(UserProfile[].class);
        verify(this.userProfilesFileRepository, times(1)).saveModelsToFile(any(String.class), argument.capture());
        assertNotNull(argument.getValue());
        assertEquals(1, argument.getValue().length);
        assertEquals("abc", argument.getValue()[0].getId());
    }

    @Test
    public void deleteUserProfileByUnknownPk() throws IOException {
        Assert.assertFalse(userProfilesService.deleteModel("ghi"));
        verify(this.userProfilesFileRepository, times(1)).loadModelsFromFile(any(String.class));
        verify(this.userProfilesFileRepository, times(0)).saveModelsToFile(any(String.class), any(UserProfile[].class));
    }

    @Test
    public void createUserProfile() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        UserProfile userProfile = this.userProfilesService.createModel();
        Assert.assertNotNull(userProfile);
        Assert.assertNotNull(userProfile.getId());
    }
}