package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class UserProfileEditFragment extends Fragment implements UserProfileEditFragmentClickListners {
    private FragmentUserprofileEditBinding binding;
    private IUserProfileEditViewModel userProfileEditViewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(this.getActivity(), "Activity must be not null");

        View root = inflater.inflate(R.layout.fragment_userprofile_edit, container, false);
        this.binding = FragmentUserprofileEditBinding.bind(root);
        this.binding.setClickListners(this);
        this.userProfileEditViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IUserProfileEditViewModel.class);
        this.userProfileEditViewModel.getUserProfileModel().observe(this, new Observer<UserProfileModel>() {
            @Override
            public void onChanged(UserProfileModel userProfileModel) {
                onUserProfileModelChanged(userProfileModel);
            }
        });
        this.userProfileEditViewModel.getAction().observe(this, new Observer<UserProfileEditFragmentAction>() {
            @Override
            public void onChanged(UserProfileEditFragmentAction action) {
                onUserProfileEditAction(action);
            }
        });
        this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);
        this.mainActivityViewModel.getAction().observe(this, new Observer<EventContent<MainActivityActionData>>() {
            @Override
            public void onChanged(EventContent<MainActivityActionData> eventContent) {
                onMainActivityAction(eventContent);
            }
        });

        if (this.getArguments() != null && this.getArguments().containsKey("userProfileId")) {
            String userProfileId = this.getArguments().getString("userProfileId");
            if (userProfileId != null) {
                this.userProfileEditViewModel.loadUserProfile(userProfileId);
            } else {
                this.userProfileEditViewModel.newUserProfile();
            }
        }
        this.mainActivityViewModel.showNewEntityButton(false);
        this.mainActivityViewModel.showSaveEntityButton(true);

        return root;
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

    @Override
    public void onUserImageClick(View view) {
            this.dispatchTakePictureIntent();
    }
}
