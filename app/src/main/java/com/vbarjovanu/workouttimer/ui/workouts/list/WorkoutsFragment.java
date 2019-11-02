package com.vbarjovanu.workouttimer.ui.workouts.list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityAction;
import com.vbarjovanu.workouttimer.MainActivityActionData;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.databinding.FragmentWorkoutsBinding;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewItemActionData;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

public class WorkoutsFragment extends Fragment {

    private IMainActivityViewModel mainActivityViewModel;
    private IWorkoutsViewModel workoutsViewModel;
    private FragmentWorkoutsBinding binding;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mainActivityViewModel != null) {
            this.mainActivityViewModel.getAction().removeObserver(this::onMainActivityAction);
        }
        if (this.workoutsViewModel != null) {
            this.workoutsViewModel.getActionData().removeObserver(this::onViewModelAction);
            this.workoutsViewModel.getWorkouts().removeObserver(this::onWorkoutsListChanged);
        }
        if (this.binding != null) {
            this.binding.getRecyclerViewAdapter().getItemAction().removeObserver(this::onRecyclerViewItemAction);
            this.binding.setRecyclerViewAdapter(null);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        if (this.getActivity() != null) {
            workoutsViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IWorkoutsViewModel.class);
            mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
            mainActivityViewModel.showNewEntityButton(true);
            mainActivityViewModel.showSaveEntityButton(false);
            root = inflater.inflate(R.layout.fragment_workouts, container, false);

            this.binding = FragmentWorkoutsBinding.bind(root);
            this.binding.setLayoutManager(new LinearLayoutManager(getContext()));
            WorkoutsRecyclerViewAdapter workoutsAdapter = new WorkoutsRecyclerViewAdapter(new WorkoutsList());
            workoutsAdapter.getItemAction().observe(this, this::onRecyclerViewItemAction);
            this.binding.setRecyclerViewAdapter(workoutsAdapter);

            this.workoutsViewModel.getWorkouts().observe(this, this::onWorkoutsListChanged);
            this.workoutsViewModel.getActionData().observe(this, this::onViewModelAction);
            this.mainActivityViewModel.getAction().observe(this, this::onMainActivityAction);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        String profileId;
        super.onActivityCreated(savedInstanceState);

        profileId = ApplicationSessionFactory.getApplicationSession(this.getContext()).getUserProfileId();
        workoutsViewModel.loadWorkouts(profileId);
    }

    private void deleteWorkoutClicked(final String workoutId) {
        final String profileId = ApplicationSessionFactory.getApplicationSession(this.getContext()).getUserProfileId();

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    workoutsViewModel.deleteWorkout(profileId, workoutId);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    private void editWorkoutClicked(String workoutId) {
        final String profileId = ApplicationSessionFactory.getApplicationSession(this.getContext()).getUserProfileId();
        workoutsViewModel.editWorkout(profileId, workoutId);
    }

    private void playWorkoutClicked(String workoutId) {
        final String profileId = ApplicationSessionFactory.getApplicationSession(this.getContext()).getUserProfileId();
        workoutsViewModel.trainWorkout(profileId, workoutId);
    }

    /**
     * When workouts list changes, the recycler view adapter is initialized
     *
     * @param workouts list of workouts
     */
    private void onWorkoutsListChanged(WorkoutsList workouts) {
        this.binding.setWorkoutsList(workouts);
    }

    /**
     * When user makes an interaction with a workout item of the recycler view
     *
     * @param workoutActionData user action data
     */
    private void onRecyclerViewItemAction(RecyclerViewItemActionData<WorkoutsRecyclerViewItemAction> workoutActionData) {
        switch (workoutActionData.getAction()) {
            case WORKOUT_SELECT:
            case WORKOUT_EDIT:
                editWorkoutClicked(workoutActionData.getId());
                break;
            case WORKOUT_PLAY:
                playWorkoutClicked(workoutActionData.getId());
                break;
            case WORKOUT_DELETE:
                deleteWorkoutClicked(workoutActionData.getId());
                break;
        }
    }

    /**
     * When the view model makes an action
     *
     * @param workoutsFragmentActionData action data
     */
    private void onViewModelAction(WorkoutsFragmentActionData workoutsFragmentActionData) {
        @SuppressWarnings("ConstantConditions")
        NavController navController = Navigation.findNavController(WorkoutsFragment.this.getActivity(), R.id.nav_host_fragment);
        Bundle args;
        switch (workoutsFragmentActionData.getAction()) {
            case GOTO_HOME:
                navController.popBackStack(R.id.nav_home, false);
                break;
            case GOTO_WORKOUT_EDIT:
            case GOTO_WORKOUT_NEW:
                args = new Bundle(3);
                args.putString("action", workoutsFragmentActionData.getAction().toString());
                args.putString("profileId", workoutsFragmentActionData.getProfileId());
                args.putString("workoutId", workoutsFragmentActionData.getWorkoutId());
                navController.navigate(R.id.action_nav_workouts_to_nav_workout_edit, args);
                break;
            case GOTO_WORKOUT_TRAINING:
                args = new Bundle(2);
                args.putString("profileId", workoutsFragmentActionData.getProfileId());
                args.putString("workoutId", workoutsFragmentActionData.getWorkoutId());
                navController.navigate(R.id.action_nav_workouts_to_nav_workout_training, args);
                break;
            case DISPLAY_WORKOUT_DELETE_FAILED:
                Toast.makeText(getContext(), "Workout delete has failed", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * When main's activity view model makes an action
     *
     * @param eventContent event data
     */
    private void onMainActivityAction(EventContent<MainActivityActionData> eventContent) {
        MainActivityActionData mainActivityActionData;
        String profileId = ApplicationSessionFactory.getApplicationSession(this.getContext()).getUserProfileId();
        mainActivityActionData = eventContent.getContent();
        if (mainActivityActionData != null && mainActivityActionData.getAction() == MainActivityAction.NEW_ENTITY_BUTTON_CLICKED) {
            eventContent.setHandled();
            WorkoutsFragment.this.workoutsViewModel.newWorkout(profileId);
        }
    }

}