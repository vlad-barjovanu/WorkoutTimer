package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.net.URL;
import java.util.List;

public class WorkoutsServiceTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void loadWorkouts() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        assert classLoader != null;
        URL resource = classLoader.getResource("business/services/workouts/Workouts.json");
        List<Workout> workoutList;
        String folderPath = resource.getPath().replace("/Workouts.json", "");
        WorkoutsFileRepository workoutsFileRepository = new WorkoutsFileRepository(Workout.class, Workout[].class);
        IFileRepositorySettings settings = new FileRepositorySettings(folderPath);
        WorkoutsService workoutsService = new WorkoutsService(workoutsFileRepository, settings, Workout.class, WorkoutsList.class);
        workoutList = workoutsService.loadModels("profile123");
        Assert.assertNotNull(workoutList);
        Assert.assertEquals(2, workoutList.size());
        Assert.assertEquals("123", workoutList.get(0).getId());
        Assert.assertEquals("workout1", workoutList.get(0).getName());
        Assert.assertEquals("456", workoutList.get(1).getId());
        Assert.assertEquals("workout2", workoutList.get(1).getName());
    }

    @Test
    public void loadWorkout() {
        Workout workout;
        ClassLoader classLoader = this.getClass().getClassLoader();
        assert classLoader != null;
        URL resource = classLoader.getResource("business/services/workouts/Workouts.json");
        String folderPath = resource.getPath().replace("/Workouts.json", "");
        WorkoutsFileRepository workoutsFileRepository = new WorkoutsFileRepository(Workout.class, Workout[].class);
        IFileRepositorySettings settings = new FileRepositorySettings(folderPath);
        WorkoutsService workoutsService = new WorkoutsService(workoutsFileRepository, settings, Workout.class, WorkoutsList.class);
        String workoutId = "456";
        workout = workoutsService.loadModel("profile123", workoutId);
        Assert.assertNotNull(workout);
        Assert.assertEquals("456", workout.getId());
        Assert.assertEquals("workout2", workout.getName());
    }

    @Test
    public void saveWorkout() {
        String profileId;
        Workout workout;
        String folderPath;
        folderPath = folder.getRoot().getPath();
        IFileRepositorySettings settings = new FileRepositorySettings(folderPath);
        WorkoutsFileRepository workoutsFileRepository = new WorkoutsFileRepository(Workout.class, Workout[].class);
        WorkoutsService workoutsService = new WorkoutsService(workoutsFileRepository, settings, Workout.class, WorkoutsList.class);

        profileId = "456";
        workout = new Workout("789");
        workout.setName("workout1").setPrepareDuration(5).setWorkDescription("push ups").setWorkDuration(10);
        workoutsService.saveModel(profileId, workout);
        File file = new File(folderPath+"/Workouts-"+profileId+".json");
        Assert.assertTrue(file.exists());
    }
}