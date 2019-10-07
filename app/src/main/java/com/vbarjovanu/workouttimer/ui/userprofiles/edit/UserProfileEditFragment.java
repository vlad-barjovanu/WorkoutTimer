package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.vbarjovanu.workouttimer.IMainActivityViewModel;
import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.IUserProfilesImagesService;
import com.vbarjovanu.workouttimer.ui.userprofiles.images.UserProfilesImagesService;

import java.io.File;
import java.util.Objects;

public class UserProfileEditFragment extends Fragment implements Observer<UserProfile>, View.OnClickListener {
    private IUserProfileEditViewModel userProfileEditViewModel;
    private IMainActivityViewModel mainActivityViewModel;
    private EditText editTextDescription;
    private EditText editTextName;
    private ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button buttonCancel;
    private Button buttonSave;
    private Bitmap newImageBitmap;
    private IUserProfilesImagesService userProfilesImagesService;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(this.getActivity(), "Activity must be not null");

        this.userProfilesImagesService = new UserProfilesImagesService(this.getContext());

        View root = inflater.inflate(R.layout.fragment_userprofile_edit, container, false);
        this.editTextName = root.findViewById(R.id.fragment_userprofile_edit_name);
        this.editTextDescription = root.findViewById(R.id.fragment_userprofile_edit_description);
        this.imageView = root.findViewById(R.id.fragment_userprofile_edit_image);
        this.buttonSave = root.findViewById(R.id.fragment_userprofile_edit_save);
        this.buttonCancel = root.findViewById(R.id.fragment_userprofile_edit_cancel);
        this.imageView.setOnClickListener(this);
        this.buttonSave.setOnClickListener(this);
        this.buttonCancel.setOnClickListener(this);


        this.userProfileEditViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IUserProfileEditViewModel.class);
        this.userProfileEditViewModel.getUserProfile().observe(this, this);
        this.userProfileEditViewModel.getAction().observe(this, new Observer<UserProfileEditFragmentAction>() {
            @Override
            public void onChanged(UserProfileEditFragmentAction userProfileEditFragmentAction) {
                NavController navController = Navigation.findNavController(UserProfileEditFragment.this.getActivity(), R.id.nav_host_fragment);
                //noinspection SwitchStatementWithTooFewBranches
                switch (userProfileEditFragmentAction) {
                    case GOTO_USERPROFILES:
                        navController.popBackStack(R.id.nav_userprofiles, false);
                        break;
                }
            }
        });
        this.mainActivityViewModel = ViewModelProviders.of(this.getActivity(), CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IMainActivityViewModel.class);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    }

    @Override
    public void onChanged(UserProfile userProfile) {
        String name = "";
        String description = "";

        if (userProfile != null) {
            name = userProfile.getName();
            description = userProfile.getDescription();
        }
        this.editTextName.setText(name);
        this.editTextDescription.setText(description);
        this.imageView.setImageBitmap( this.userProfilesImagesService.getUserImage(userProfile));
    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.imageView)) {
            this.dispatchTakePictureIntent();
        }
        if (view.equals(this.buttonSave)) {
            String name = this.editTextName.getText().toString();
            String description = this.editTextDescription.getText().toString();
            this.userProfileEditViewModel.saveUserProfile(name, description, this.newImageBitmap);
        }
        if (view.equals(this.buttonCancel)) {
            this.userProfileEditViewModel.cancelUserProfileEdit();
        }
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
                this.newImageBitmap = (Bitmap) extras.get("data");
                this.imageView.setImageBitmap(this.newImageBitmap);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
