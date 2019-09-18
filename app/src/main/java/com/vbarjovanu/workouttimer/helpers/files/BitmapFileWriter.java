package com.vbarjovanu.workouttimer.helpers.files;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapFileWriter {
    public void writeFile(String filePath, Bitmap content, Bitmap.CompressFormat compressFormat, int quality) throws IOException {
        File file = new File(filePath);
        FolderHelper.mkdir(file.getParentFile().getAbsolutePath(), true);
        OutputStream os = new FileOutputStream(file);
        content.compress(compressFormat, quality, os);
        os.flush();
        os.close();
    }
}
