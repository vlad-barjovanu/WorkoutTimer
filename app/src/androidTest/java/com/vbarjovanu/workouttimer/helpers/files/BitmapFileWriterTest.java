package com.vbarjovanu.workouttimer.helpers.files;

import android.graphics.Bitmap;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static android.graphics.Bitmap.Config.ARGB_8888;
import static org.junit.Assert.*;

public class BitmapFileWriterTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void writeFile() {
        BitmapFileWriter bitmapFileWriter = new BitmapFileWriter();
        File tempFile;
        String filePath;
        Bitmap content = Bitmap.createBitmap(10,10,ARGB_8888);

        try {
            tempFile = folder.newFile("file.bin");
            filePath = tempFile.getPath();
            bitmapFileWriter.writeFile(filePath, content, Bitmap.CompressFormat.PNG, 100);
            File file = new File(filePath);
            Assert.assertTrue(file.exists());
            Assert.assertEquals(87, file.length());
        } catch (IOException e) {
            Assert.assertNull(e);
        }
    }

}