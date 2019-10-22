package com.vbarjovanu.workouttimer.ui.userprofiles.edit;

import android.graphics.Bitmap;

import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserProfileModelTest {
    private UserProfileModel userProfileModel;
    private Bitmap userImage;
    private UserProfile userProfile;

    @Before
    public void setUp() {
        this.userProfile = new UserProfile("abc");
        this.userImage = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.userProfileModel = new UserProfileModel(this.userProfile, this.userImage);
    }

    @After
    public void tearDown() {
        this.userImage = null;
        this.userProfile = null;
        this.userProfileModel = null;
    }

    @Test
    public void getUserProfile() {
        Assert.assertNotNull(this.userProfileModel.getUserProfile());
        Assert.assertEquals(this.userProfile, this.userProfileModel.getUserProfile());
    }

    @Test
    public void getUserImage() {
        Assert.assertNotNull(this.userProfileModel.getUserImage());
        Assert.assertEquals(this.userImage, this.userProfileModel.getUserImage());
    }

    @Test
    public void setUserImage() {
        Bitmap newUserImage = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_4444);
        Assert.assertEquals(this.userProfileModel, this.userProfileModel.setUserImage(newUserImage));
        Assert.assertEquals(newUserImage, this.userProfileModel.getUserImage());
    }
}