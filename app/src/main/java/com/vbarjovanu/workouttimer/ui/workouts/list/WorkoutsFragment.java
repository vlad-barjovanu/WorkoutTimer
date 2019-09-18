package com.vbarjovanu.workouttimer.ui.workouts.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.workouts.WorkoutsList;
import com.vbarjovanu.workouttimer.session.ApplicationSessionFactory;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

public class WorkoutsFragment extends Fragment {

    private IWorkoutsViewModel workoutsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String profileId;
        workoutsViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IWorkoutsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_workouts, container, false);
        final TextView textView = root.findViewById(R.id.text_workouts);
        workoutsViewModel.getWorkouts().observe(this, new Observer<WorkoutsList>() {
            @Override
            public void onChanged(@Nullable WorkoutsList workouts) {
                textView.setText("Workouts");
            }
        });

        profileId = ApplicationSessionFactory.getApplicationSession(this.getContext()).getUserProfileId();
        workoutsViewModel.loadWorkouts(profileId);
        return root;
    }
}