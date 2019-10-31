package com.vbarjovanu.workouttimer.ui.workouts.edit;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityActionData;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.workouts.Workout;
import com.vbarjovanu.workouttimer.databinding.FragmentWorkoutEditBinding;
import com.vbarjovanu.workouttimer.ui.colors.ColorsPickerDialogFragment;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

import java.util.Objects;

public class WorkoutEditFragment extends Fragment implements WorkoutEditFragmentClickListners {

    private IWorkoutEditViewModel workoutEditViewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private FragmentWorkoutEditBinding binding;
    private Observer<? super Workout> workoutObserver;
    private Observer<? super WorkoutEditFragmentAction> actionObserver;
    private Observer<? super EventContent<MainActivityActionData>> mainActivityActionObserver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = null;

        if (this.getActivity() != null) {
            this.workoutEditViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IWorkoutEditViewModel.class);
            this.addViewModelWorkoutObserver();
            this.addViewModelActionObserver();
            this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
            this.mainActivityViewModel.showNewEntityButton(false);
            this.mainActivityViewModel.showSaveEntityButton(true);
            this.addMainActivityViewModelActionObserver();

            root = inflater.inflate(R.layout.fragment_workout_edit, container, false);
            this.binding = FragmentWorkoutEditBinding.bind(root);
            this.binding.setClickListners(this);
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getArguments() != null) {
            String action = this.getArguments().getString("action", "");
            String workoutId = this.getArguments().getString("workoutId", null);
            if (action.equals("GOTO_WORKOUT_EDIT") && workoutId != null) {
                this.loadWorkout(workoutId, savedInstanceState);
            }
            if (action.equals("GOTO_WORKOUT_NEW")) {
                this.newWorkout(savedInstanceState);
            }
        }
    }

    private Workout getSavedWorkout(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey("workout")) {
            return (Workout) savedInstanceState.getSerializable("workout");
        }
        return null;
    }

    private void newWorkout(Bundle savedInstanceState) {
        if (!this.workoutEditViewModel.isInitialised()) {
            this.workoutEditViewModel.newWorkout(this.getSavedWorkout(savedInstanceState));
        }
    }

    private void loadWorkout(String workoutId, @Nullable Bundle savedInstanceState) {
        if (!this.workoutEditViewModel.isInitialised()) {
            this.workoutEditViewModel.loadWorkout(workoutId, this.getSavedWorkout(savedInstanceState));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.workoutEditViewModel.isInitialised()) {
            outState.putSerializable("workout", this.workoutEditViewModel.getWorkout().getValue());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.removeViewModelWorkoutObserver();
        this.removeViewModelActionObserver();
        this.removeMainActivityViewModelActionObserver();
    }

    private void removeViewModelWorkoutObserver() {
        if (this.workoutEditViewModel != null && this.workoutObserver != null) {
            this.workoutEditViewModel.getWorkout().removeObserver(this.workoutObserver);
            this.workoutObserver = null;
        }
    }

    private void addViewModelWorkoutObserver() {
        this.workoutObserver = (Observer<Workout>) this::onWorkoutChanged;
        this.workoutEditViewModel.getWorkout().observe(this, this.workoutObserver);
    }

    private void removeViewModelActionObserver() {
        if (this.workoutEditViewModel != null && this.actionObserver != null) {
            this.workoutEditViewModel.getAction().removeObserver(this.actionObserver);
            this.actionObserver = null;
        }
    }

    private void addViewModelActionObserver() {
        this.actionObserver = (Observer<WorkoutEditFragmentAction>) this::onActionChanged;
        this.workoutEditViewModel.getAction().observe(this, this.actionObserver);
    }

    private void removeMainActivityViewModelActionObserver() {
        if (this.mainActivityViewModel != null && this.mainActivityActionObserver != null) {
            this.mainActivityViewModel.getAction().removeObserver(this.mainActivityActionObserver);
            this.mainActivityActionObserver = null;
        }
    }

    private void addMainActivityViewModelActionObserver() {
        this.mainActivityActionObserver = (Observer<EventContent<MainActivityActionData>>) this::onMainActivityActionChanged;
        this.mainActivityViewModel.getAction().observe(this, this.mainActivityActionObserver);
    }

    private void onActionChanged(WorkoutEditFragmentAction workoutEditFragmentAction) {
        if (this.getActivity() != null) {
            NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment);
            //noinspection SwitchStatementWithTooFewBranches
            switch (workoutEditFragmentAction) {
                case GOTO_WORKOUTS:
                    navController.popBackStack(R.id.nav_workouts, false);
                    break;
            }
        }
    }

    private void onWorkoutChanged(Workout workout) {
        this.binding.setWorkout(workout);
    }

    private void onMainActivityActionChanged(@NonNull EventContent<MainActivityActionData> eventContent) {
        MainActivityActionData mainActivityActionData = eventContent.getContent();
        if (mainActivityActionData != null) {
            switch (mainActivityActionData.getAction()) {
                case SAVE_ENTITY_BUTTON_CLICKED:
                    eventContent.setHandled();
                    hideKeyboard();
                    Workout workout = this.binding.getWorkout();
                    this.workoutEditViewModel.saveWorkout(workout);
                    break;
                case CANCEL_ENTITY_EDIT_BUTTON_CLICKED:
                    eventContent.setHandled();
                    hideKeyboard();
                    this.workoutEditViewModel.cancelWorkoutEdit();
                    break;
            }
        }
    }

    private void hideKeyboard() {
        if (this.getContext() != null && this.getView() != null) {
            InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);
        }
    }

    @Override
    public void onImageViewColorClick(View view) {
        final Workout workout;
        Objects.requireNonNull(getFragmentManager(), "FragmentManager must be not null");
        final ColorsPickerDialogFragment dialog = new ColorsPickerDialogFragment();
        Bundle arguments = new Bundle(1);
        workout = this.workoutEditViewModel.getWorkout().getValue();
        if (workout != null) {
            arguments.putIntArray("colors", this.workoutEditViewModel.getWorkoutPossibleColors());
            arguments.putInt("color", workout.getColor());
            dialog.setArguments(arguments);
            dialog.show(getFragmentManager(), "ColorsPickerDialogFragment");
            dialog.setOnDismissListener(dlg -> {
                if (dialog.getArguments() != null) {
                    int color = dialog.getArguments().getInt("color");
                    workout.setColor(color);
                    WorkoutEditFragment.this.binding.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.workout);
                    WorkoutEditFragment.this.binding.invalidateAll();
                }
            });
        }
    }
}
