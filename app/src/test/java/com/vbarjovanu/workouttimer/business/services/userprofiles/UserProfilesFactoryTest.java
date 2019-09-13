package com.vbarjovanu.workouttimer.business.services.userprofiles;

import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.isA;

public class UserProfilesFactoryTest {

    @Test
    public void getUserProfilesService() {
        IFileRepositorySettings fileRepoSettings;
        fileRepoSettings = new FileRepositorySettings("");
        IUserProfilesService userProfileService = UserProfilesFactory.getUserProfilesService(fileRepoSettings);
        Assert.assertNotNull(userProfileService);
        Assert.assertThat(userProfileService, isA(IUserProfilesService.class));
    }

    @Test
    public void getUserProfilesFileRepository() {
        IUserProfilesFileRepository fileRepo = UserProfilesFactory.getUserProfilesFileRepository();
        Assert.assertNotNull(fileRepo);
        Assert.assertThat(fileRepo, isA(IUserProfilesFileRepository.class));
    }
}