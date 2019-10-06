package com.vbarjovanu.workouttimer.business.services.workouts;

import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.business.services.generic.FileRepositorySettings;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.helpers.files.TextFileReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

import static com.vbarjovanu.workouttimer.helpers.MockitoUtils.mock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class WorkoutsServiceTest {

    @Mock
    private IFileRepositorySettings settings;
    @Mock
    private IWorkoutsFileRepository workoutsFileRepository;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private IWorkoutsService workoutsService;

    @Before
    public void setup() throws IOException {
        this.setupFileRepoSettings();
        this.setupWorkoutsFileRepo();
        this.workoutsService = new WorkoutsService(this.workoutsFileRepository, settings, Workout.class, WorkoutsList.class);
    }

    private void setupFileRepoSettings() {
        this.settings = mock(IFileRepositorySettings.class);
        Mockito.when(this.settings.getFolderPath()).thenReturn(this.folder.getRoot().getAbsolutePath());
    }

    private void setupWorkoutsFileRepo() throws IOException {
        Workout[] workoutsList;
        workoutsList = new Workout[2];
        workoutsList[0] = new Workout("123").setName("workout1");
        workoutsList[1] = new Workout("456").setName("workout2");
        this.workoutsFileRepository = mock(IWorkoutsFileRepository.class);
        Mockito.when(this.workoutsFileRepository.loadModelsFromFile(any(String.class))).thenReturn(workoutsList);
    }

    @Test
    public void loadWorkouts() {
        WorkoutsList workoutList = this.workoutsService.loadModels("profile123");
        Assert.assertNotNull(workoutList);
        Assert.assertEquals(2, workoutList.size());
        Assert.assertEquals("123", workoutList.get(0).getId());
        Assert.assertEquals("workout1", workoutList.get(0).getName());
        Assert.assertEquals("456", workoutList.get(1).getId());
        Assert.assertEquals("workout2", workoutList.get(1).getName());
    }

    @Test
    public void loadWorkout() {
        String workoutId = "456";
        Workout workout = this.workoutsService.loadModel("profile123", workoutId);
        Assert.assertNotNull(workout);
        Assert.assertEquals("456", workout.getId());
        Assert.assertEquals("workout2", workout.getName());
    }

    @Test
    public void saveWorkout() throws IOException {
        String profileId = "456";
        ArgumentCaptor<Workout[]> argument;
        Workout workout = new Workout("789");
        workout.setName("workout1").setPrepareDuration(5).setWorkDescription("push ups").setWorkDuration(10);
        assertTrue(workoutsService.saveModel(profileId, workout));
        verify(this.workoutsFileRepository, times(1)).loadModelsFromFile(any(String.class));
        argument = ArgumentCaptor.forClass(Workout[].class);
        verify(this.workoutsFileRepository, times(1)).saveModelsToFile(any(String.class), argument.capture());
        assertNotNull(argument.getValue());
        assertEquals(3, argument.getValue().length);
        assertEquals("789", argument.getValue()[2].getId());
    }

    @Test
    public void deleteWorkout() throws IOException {
        ArgumentCaptor<Workout[]> argument;

        /*
         * load workout workout with ID 123 and delete it
         * check that workoutsFileRepository will save a list with only 1 workout with ID 456
         */

        Workout workout = this.workoutsService.loadModel("profile123", "123");
        Assert.assertNotNull(workout);
        assertTrue(this.workoutsService.deleteModel("profile123", workout));
        verify(this.workoutsFileRepository, times(2)).loadModelsFromFile(any(String.class));
        argument = ArgumentCaptor.forClass(Workout[].class);
        verify(this.workoutsFileRepository, times(1)).saveModelsToFile(any(String.class), argument.capture());
        assertNotNull(argument.getValue());
        assertEquals(1, argument.getValue().length);
        assertEquals("456", argument.getValue()[0].getId());
    }

    @Test
    public void deleteUnknownWorkout() throws IOException {
        /*
         * load workout workout with ID 123 and delete it
         * check that workoutsFileRepository will save a list with only 1 workout with ID 456
         */

        Workout workout = new Workout("789");
        assertFalse(this.workoutsService.deleteModel("profile123", workout));
        verify(this.workoutsFileRepository, times(1)).loadModelsFromFile(any(String.class));
        verify(this.workoutsFileRepository, times(0)).saveModelsToFile(any(String.class), any(Workout[].class));
    }

    @Test
    public void deleteWorkoutByPk() throws IOException {
        ArgumentCaptor<Workout[]> argument;

        /*
         * delete workout with ID 123
         * check that workoutsFileRepository will save a list with only 1 workout with ID 456
         */

        assertTrue(this.workoutsService.deleteModel("profile123", "123"));
        verify(this.workoutsFileRepository, times(1)).loadModelsFromFile(any(String.class));
        argument = ArgumentCaptor.forClass(Workout[].class);
        verify(this.workoutsFileRepository, times(1)).saveModelsToFile(any(String.class), argument.capture());
        assertNotNull(argument.getValue());
        assertEquals(1, argument.getValue().length);
        assertEquals("456", argument.getValue()[0].getId());
    }

    @Test
    public void deleteWorkoutByUnknownPk() throws IOException {
        /*
         * delete workout with ID 789
         * check that workoutsFileRepository will not save any list
         */

        assertFalse(this.workoutsService.deleteModel("profile123", "789"));
        verify(this.workoutsFileRepository, times(1)).loadModelsFromFile(any(String.class));
        verify(this.workoutsFileRepository, times(0)).saveModelsToFile(any(String.class), any(Workout[].class));
    }

    @Test
    public void createWorkout() throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException {
        Workout workout;

        workout = this.workoutsService.createModel();
        Assert.assertNotNull(workout);
        Assert.assertNotNull(workout.getId());
        verify(this.workoutsFileRepository, times(0)).loadModelsFromFile(any(String.class));
        verify(this.workoutsFileRepository, times(0)).saveModelsToFile(any(String.class), any(Workout[].class));
    }

    @Test
    public void getWorkoutsCount() {
        assertEquals(2, this.workoutsService.getWorkoutsCount("profile123"));
    }
}