package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.vbarjovanu.workouttimer.BR;
import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.MainActivityActionData;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.databinding.FragmentUserprofileEditBinding;
import com.vbarjovanu.workouttimer.ui.generic.events.EventContent;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.IUserProfilesImagesService;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.UserProfilesImagesService;

import java.util.Objects;

public class UserProfileEditFragment extends Fragment {
    private FragmentUserprofileEditBinding binding;
    private IUserProfileEditViewModel userProfileEditViewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(this.getActivity(), "Activity must be not null");

        View root = inflater.inflate(R.layout.fragment_userprofile_edit, container, false);
        this.binding = FragmentUserprofileEditBinding.bind(root);
        this.setBindingClickListeners();
        this.userProfileEditViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IUserProfileEditViewModel.class);
        this.userProfileEditViewModel.getUserProfileModel().observe(this, this::onUserProfileModelChanged);
        this.userProfileEditViewModel.getAction().observe(this, this::onUserProfileEditAction);
        this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
        this.mainActivityViewModel.getAction().observe(this, this::onMainActivityAction);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (this.getArguments() != null) {

            String action = this.getArguments().getString("action", "");
            String userProfileId = this.getArguments().getString("userProfileId", null);

            if (action.equals("GOTO_USERPROFILE_EDIT") && userProfileId != null) {
                this.loadUserProfile(userProfileId, savedInstanceState);
            }
            if (action.equals("GOTO_USERPROFILE_NEW")) {
                this.newUserProfile(savedInstanceState);
            }
        }
        this.mainActivityViewModel.showNewEntityButton(false);
        this.mainActivityViewModel.showSaveEntityButton(true);
    }

    private void newUserProfile(Bundle savedInstanceState) {
        UserProfile savedUserProfile = null;
        if (!this.userProfileEditViewModel.isInitialised()) {
            if (savedInstanceState != null && savedInstanceState.containsKey("userProfile")) {
                savedUserProfile = (UserProfile) savedInstanceState.getSerializable("userProfile");
            }
            this.userProfileEditViewModel.newUserProfile(savedUserProfile);
        }
    }

    private void loadUserProfile(String userProfileId, @Nullable Bundle savedInstanceState) {
        UserProfile savedUserProfile = null;
        if (!this.userProfileEditViewModel.isInitialised()) {
            if (savedInstanceState != null && savedInstanceState.containsKey("userProfile")) {
                savedUserProfile = (UserProfile) savedInstanceState.getSerializable("userProfile");
            }
            this.userProfileEditViewModel.loadUserProfile(userProfileId, savedUserProfile);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.userProfileEditViewModel.isInitialised()) {
            //noinspection ConstantConditions
            outState.putSerializable("userProfile", this.userProfileEditViewModel.getUserProfileModel().getValue().getUserProfile());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.getActivity() != null && !this.getActivity().isChangingConfigurations()) {
            hideKeyboard();
            if (this.userProfileEditViewModel != null && this.userProfileEditViewModel.isInitialised()) {
                this.userProfileEditViewModel.cancelUserProfileEdit();
            }
        }
    }

    private void setBindingClickListeners() {
        this.binding.setClickListners(new UserProfileEditFragmentClickListners() {
            @Override
            public void onUserImageClick(View view) {
                UserProfileEditFragment.this.onUserImageClick(view);
            }

            @Override
            public void onDeleteUserImageClick(View view) {
                UserProfileEditFragment.this.onDeleteUserImageClick(view);
            }
        });

    }

    private void onUserProfileModelChanged(UserProfileModel userProfileModel) {
        this.binding.setUserProfileModel(userProfileModel);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (this.getActivity() != null && takePictureIntent.resolveActivity(this.getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                this.binding.getUserProfileModel().setUserImage((Bitmap) extras.get("data"));
                this.binding.notifyPropertyChanged(com.vbarjovanu.workouttimer.BR.userProfileModel);
                this.binding.invalidateAll();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onUserProfileEditAction(UserProfileEditFragmentAction action) {
        Objects.requireNonNull(this.getActivity(), "Activity must not be null");
        NavController navController = Navigation.findNavController(this.getActivity(), R.id.nav_host_fragment);
        //noinspection SwitchStatementWithTooFewBranches
        switch (action) {
            case GOTO_USERPROFILES:
                navController.popBackStack(R.id.nav_userprofiles, false);
                break;
        }
    }

    private void onMainActivityAction(@NonNull EventContent<MainActivityActionData> eventContent) {
        MainActivityActionData mainActivityActionData = eventContent.getContent();
        if (mainActivityActionData != null) {
            switch (mainActivityActionData.getAction()) {
                case SAVE_ENTITY_BUTTON_CLICKED:
                    eventContent.setHandled();
                    hideKeyboard();
                    this.userProfileEditViewModel.saveUserProfile(this.binding.getUserProfileModel());
                    break;
                case CANCEL_ENTITY_EDIT_BUTTON_CLICKED:
                    eventContent.setHandled();
                    hideKeyboard();
                    this.userProfileEditViewModel.cancelUserProfileEdit();
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

    private void onUserImageClick(View view) {
        this.dispatchTakePictureIntent();
    }

    private void onDeleteUserImageClick(View view) {
        this.userProfileEditViewModel.deleteUserImage();
    }
}
