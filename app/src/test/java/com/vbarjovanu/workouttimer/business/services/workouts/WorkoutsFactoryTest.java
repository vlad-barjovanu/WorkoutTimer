package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.*;

public class WorkoutsFactoryTest {

    @Test
    public void getWorkoutsService() {
        IFileRepositorySettings fileRepoSettings;
        fileRepoSettings = new FileRepositorySettings("");
        IWorkoutsService workoutsService = WorkoutsFactory.getWorkoutsService(fileRepoSettings);
        Assert.assertNotNull(workoutsService);
        Assert.assertThat(workoutsService, isA(IWorkoutsService.class));
    }

    @Test
    public void getWorkoutsFileRepository() {
        IWorkoutsFileRepository fileRepo = WorkoutsFactory.getWorkoutsFileRepository();
        Assert.assertNotNull(fileRepo);
        Assert.assertThat(fileRepo, isA(IWorkoutsFileRepository.class));
    }
}