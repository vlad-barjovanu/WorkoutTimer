package com.vbarjovanu.workouttimer.ui.workouts.list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityAction;
import com.vbarjovanu.workouttimer.MainActivityActionData;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.recyclerview.RecyclerViewItemActionData;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

public class WorkoutsFragment extends Fragment {

    private IMainActivityViewModel mainActivityViewModel;
    private WorkoutsRecyclerViewAdapter workoutsAdapter;
    private IWorkoutsViewModel workoutsViewModel;
    private RecyclerView recyclerView;
    private TextView textViewDescription;

    private Observer<EventContent<MainActivityActionData>> mainActivityViewModelObserver;
    private Observer<WorkoutsFragmentActionData> workoutsViewModelActionDataObserver;
    private Observer<WorkoutsList> workoutsListObserver;
    private Observer<RecyclerViewItemActionData<WorkoutsRecyclerViewItemAction>> workoutsAdapterItemActionObserver;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.removeMainActivityActionObserver();
        this.removeWorkoutsViewModelActionDataObserver();
        this.removeWorkoutsViewModelWorkoutsObserver();
        this.removeWorkoutsAdapter(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        if (this.getActivity() != null) {
            workoutsViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IWorkoutsViewModel.class);
            mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
            mainActivityViewModel.showNewEntityButton(true);
            mainActivityViewModel.showSaveEntityButton(false);
            root = inflater.inflate(R.layout.fragment_workouts, container, false);
            textViewDescription = root.findViewById(R.id.text_workouts_description);
            textViewDescription.setText(getString(R.string.message_no_workouts_are_defined));
            textViewDescription.setVisibility(View.GONE);
            this.recyclerView = root.findViewById(R.id.recyclerview_workouts);
            this.recyclerView.setHasFixedSize(true);
            this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            this.addWorkoutsViewModelWorkoutsObserver();
            this.addWorkoutsViewModelActionDataObserver();
            this.addMainActivityActionObserver();
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

    private void removeWorkoutsAdapterActionObserver() {
        if (this.workoutsAdapter != null && this.workoutsAdapterItemActionObserver != null) {
            this.workoutsAdapter.getItemAction().removeObserver(this.workoutsAdapterItemActionObserver);
            this.workoutsAdapterItemActionObserver = null;
        }
    }

    private void addWorkoutsAdapterItemActionObserver() {
        this.workoutsAdapterItemActionObserver = this::onWorkoutsRecyclerViewItemActionDataChanged;
        this.workoutsAdapter.getItemAction().observe(this, this.workoutsAdapterItemActionObserver);
    }

    private void removeWorkoutsAdapter(boolean removeAdapter) {
        this.removeWorkoutsAdapterActionObserver();
        if (removeAdapter) {
            this.recyclerView.setAdapter(null);
        }
    }

    private void addWorkoutsAdapter(WorkoutsList workouts) {
        this.removeWorkoutsAdapter(false);
        this.workoutsAdapter = new WorkoutsRecyclerViewAdapter(workouts);
        this.addWorkoutsAdapterItemActionObserver();
        this.recyclerView.swapAdapter(this.workoutsAdapter, false);
    }

    private void removeWorkoutsViewModelWorkoutsObserver() {
        if (this.workoutsViewModel != null && this.workoutsListObserver != null) {
            this.workoutsViewModel.getWorkouts().removeObserver(this.workoutsListObserver);
            this.workoutsListObserver = null;
        }
    }

    private void addWorkoutsViewModelWorkoutsObserver() {
        this.workoutsListObserver = WorkoutsFragment.this::onWorkoutsListChanged;
        this.workoutsViewModel.getWorkouts().observe(this, this.workoutsListObserver);
    }

    private void removeWorkoutsViewModelActionDataObserver() {
        if (workoutsViewModel != null && this.workoutsViewModelActionDataObserver != null) {
            workoutsViewModel.getActionData().removeObserver(this.workoutsViewModelActionDataObserver);
            this.workoutsViewModelActionDataObserver = null;
        }
    }

    private void addWorkoutsViewModelActionDataObserver() {
        this.workoutsViewModelActionDataObserver = this::onWorkoutsFragmentActionDataChanged;
        this.workoutsViewModel.getActionData().observe(this, this.workoutsViewModelActionDataObserver);
    }

    private void removeMainActivityActionObserver() {
        if (this.mainActivityViewModel != null && this.mainActivityViewModelObserver != null) {
            this.mainActivityViewModel.getAction().removeObserver(this.mainActivityViewModelObserver);
            this.mainActivityViewModelObserver = null;
        }
    }

    private void addMainActivityActionObserver() {
        this.mainActivityViewModelObserver = this::onMainActivityActionDataChanged;
        this.mainActivityViewModel.getAction().observe(this, this.mainActivityViewModelObserver);
    }

    /**
     * When workouts list changes, the recycler view adapter is initialized
     *
     * @param workouts list of workouts
     */
    private void onWorkoutsListChanged(WorkoutsList workouts) {
        this.addWorkoutsAdapter(workouts);
        if (workouts.size() == 0) {
            this.textViewDescription.setVisibility(View.VISIBLE);
            this.recyclerView.setVisibility(View.GONE);
        } else {
            this.textViewDescription.setVisibility(View.GONE);
            this.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * When user makes an interaction with a workout item of the recycler view
     *
     * @param workoutActionData user action data
     */
    private void onWorkoutsRecyclerViewItemActionDataChanged(RecyclerViewItemActionData<WorkoutsRecyclerViewItemAction> workoutActionData) {
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
    private void onWorkoutsFragmentActionDataChanged(WorkoutsFragmentActionData workoutsFragmentActionData) {
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
    private void onMainActivityActionDataChanged(EventContent<MainActivityActionData> eventContent) {
        MainActivityActionData mainActivityActionData;
        String profileId = ApplicationSessionFactory.getApplicationSession(this.getContext()).getUserProfileId();
        mainActivityActionData = eventContent.getContent();
        if (mainActivityActionData != null && mainActivityActionData.getAction() == MainActivityAction.NEW_ENTITY_BUTTON_CLICKED) {
            eventContent.setHandled();
            WorkoutsFragment.this.workoutsViewModel.newWorkout(profileId);
        }
    }

}