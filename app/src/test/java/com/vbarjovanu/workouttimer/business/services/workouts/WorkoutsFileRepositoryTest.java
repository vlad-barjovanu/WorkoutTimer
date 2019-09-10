package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

public class WorkoutsFileRepositoryTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void loadWorkoutsFile() {
        WorkoutsFileRepository workoutsFileRepository = new WorkoutsFileRepository(Workout.class, Workout[].class);
        Workout[] workouts = null;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            assert classLoader != null;
            URL resource = classLoader.getResource("business/services/workouts/Workouts.json");
            workouts = workoutsFileRepository.loadModelsFromFile(resource.getPath());
        } catch (IOException e) {
            Assert.assertNull(e);
        }
        Assert.assertNotNull(workouts);
        Assert.assertEquals(2, workouts.length);
        Assert.assertEquals("123", workouts[0].getId());
        Assert.assertEquals("workout1", workouts[0].getName());
        Assert.assertEquals("456", workouts[1].getId());
        Assert.assertEquals("workout2", workouts[1].getName());
    }

    @Test
    public void saveWorkoutsFile() {
        WorkoutsFileRepository workoutsFileRepository = new WorkoutsFileRepository(Workout.class, Workout[].class);
        String filePath = null;
        Workout[] workoutsLoaded = null;

        Workout[] workouts = new Workout[2];
        workouts[0] = new Workout("123");
        workouts[0].setName("workout1");
        workouts[1] = new Workout("456");
        workouts[1].setName("workout2");

        try {
            File tempFile = folder.newFile("file.txt");
            filePath = tempFile.getPath();
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }
        try {
            workoutsFileRepository.saveModelsToFile(filePath, workouts);
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }

        try {
            workoutsLoaded = workoutsFileRepository.loadModelsFromFile(filePath);
        } catch (IOException e) {
            Assert.assertNull(e.getMessage(), e);
        }
        Assert.assertNotNull(workoutsLoaded);
        Assert.assertEquals(2, workoutsLoaded.length);
        for (int i = 0; i < 2; i++) {
            Assert.assertEquals(workouts[i].getId(), workoutsLoaded[i].getId());
            Assert.assertEquals(workouts[i].getName(), workoutsLoaded[i].getName());
        }
    }
}