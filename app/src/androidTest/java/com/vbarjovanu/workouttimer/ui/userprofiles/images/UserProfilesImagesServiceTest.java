package com.vbarjovanu.workouttimer.ui.userprofiles.images;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.platform.app.InstrumentationRegistry;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.business.models.userprofiles.UserProfile;
import com.vbarjovanu.workouttimer.business.services.generic.IFileRepositorySettings;
import com.vbarjovanu.workouttimer.helpers.assets.AssetsFileExporter;
import com.vbarjovanu.workouttimer.helpers.images.BitmapHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UserProfilesImagesServiceTest {
    private IUserProfilesImagesService userProfilesImagesService;
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        IFileRepositorySettings fileRepoSettings;
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        fileRepoSettings = mock(IFileRepositorySettings.class);
        Mockito.when(fileRepoSettings.getFolderPath()).thenReturn(this.folder.getRoot().getAbsolutePath());
        this.userProfilesImagesService = new UserProfilesImagesService(context, fileRepoSettings);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void getUserImage() throws IOException {
        /*
         * call getUserImage() for a userProfile that has a valid ImageFilePath
         * expects: a bitmap is returned
         * expects: the returned bitmap is not the default user image
         */
        UserProfile userProfile;
        userProfile = new UserProfile("abc");
        String filePath = this.folder.getRoot().getAbsolutePath() + "picture.png";
        Bitmap userImage;
        Bitmap defaultUserImage;

        AssetsFileExporter assetsFileExporter;
        assetsFileExporter = new AssetsFileExporter(InstrumentationRegistry.getInstrumentation().getContext());
        assetsFileExporter.exportAsset("business/services/userprofiles/images/picture.png", filePath);
        userProfile.setImageFilePath(filePath);
        userImage = this.userProfilesImagesService.getUserImage(userProfile);
        assertNotNull(userImage);
        defaultUserImage = BitmapHelper.fromResource(InstrumentationRegistry.getInstrumentation().getTargetContext(), R.drawable.userprofile);
        assertNotNull(defaultUserImage);
        assertNotEquals(userImage.getHeight(), defaultUserImage.getHeight());
        assertNotEquals(userImage.getWidth(), defaultUserImage.getWidth());
        assertNotEquals(userImage.getByteCount(), defaultUserImage.getByteCount());
    }

    @Test
    public void getUserImageNoImageFilePath() {
        /*
         * call getUserImage() for a userProfile that has no ImageFilePath (is null)
         * expects: a bitmap is returned
         * expects: the returned bitmap is the default user image
         */
        UserProfile userProfile;
        userProfile = new UserProfile("abc");
        String filePath = null;
        Bitmap userImage;
        Bitmap defaultUserImage;

        userProfile.setImageFilePath(filePath);
        userImage = this.userProfilesImagesService.getUserImage(userProfile);
        assertNotNull(userImage);
        defaultUserImage = BitmapHelper.fromResource(InstrumentationRegistry.getInstrumentation().getTargetContext(), R.drawable.userprofile);
        assertNotNull(defaultUserImage);
        assertEquals(userImage.getHeight(), defaultUserImage.getHeight());
        assertEquals(userImage.getWidth(), defaultUserImage.getWidth());
        assertEquals(userImage.getByteCount(), defaultUserImage.getByteCount());
    }

    @Test
    public void getUserImageWrongImageFilePath() {
        /*
         * call getUserImage() for a userProfile that has a invalid ImageFilePath (not null)
         * expects: a bitmap is returned
         * expects: the returned bitmap is the default user image
         */
        UserProfile userProfile;
        userProfile = new UserProfile("abc");
        String filePath = "/wrong/filepath/file.png";
        Bitmap userImage;
        Bitmap defaultUserImage;

        userProfile.setImageFilePath(filePath);
        userImage = this.userProfilesImagesService.getUserImage(userProfile);
        assertNotNull(userImage);
        defaultUserImage = BitmapHelper.fromResource(InstrumentationRegistry.getInstrumentation().getTargetContext(), R.drawable.userprofile);
        assertNotNull(defaultUserImage);
        assertEquals(userImage.getHeight(), defaultUserImage.getHeight());
        assertEquals(userImage.getWidth(), defaultUserImage.getWidth());
        assertEquals(userImage.getByteCount(), defaultUserImage.getByteCount());
    }

    @Test
    public void setUserImage() {
        /*
         * call setUserImage() and send a valid Bitmap
         * expects: the Bitmap is saved in a file and the filePath is updated into the UserProfile
         */
        UserProfile userProfile;
        userProfile = new UserProfile("abc").setImageFilePath(null);
        String filePath;
        Bitmap defaultUserImage;
        File file;

        //check that there is no previous file
        filePath = this.folder.getRoot().getAbsolutePath() + "/images/" + userProfile.getId() + ".png";
        file = new File(filePath);
        assertFalse(file.exists());
        defaultUserImage = BitmapHelper.fromResource(InstrumentationRegistry.getInstrumentation().getTargetContext(), R.drawable.userprofile);
        assertNotNull(defaultUserImage);
        this.userProfilesImagesService.setUserImage(userProfile, defaultUserImage);
        //check that userProfile ImageFilePath is updated as expected and points to a valid file
        assertNotNull(userProfile.getImageFilePath());
        assertEquals(filePath, userProfile.getImageFilePath());
        file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    public void setUserImageNull() {
        /*
         * call setUserImage() and send a null Bitmap
         * expects: the Bitmap is not saved to a file and the the UserProfile ImageFilePath is set to null
         */
        UserProfile userProfile;
        userProfile = new UserProfile("abc").setImageFilePath(null);
        String filePath;
        Bitmap userImage = null;
        File file;

        //check that there is no previous file
        filePath = this.folder.getRoot().getAbsolutePath() + "/images/" + userProfile.getId() + ".png";
        file = new File(filePath);
        assertFalse(file.exists());
        this.userProfilesImagesService.setUserImage(userProfile, userImage);
        //check that userProfile ImageFilePath is updated as expected and points to a valid file
        assertNull(userProfile.getImageFilePath());
        file = new File(filePath);
        assertFalse(file.exists());
    }
}