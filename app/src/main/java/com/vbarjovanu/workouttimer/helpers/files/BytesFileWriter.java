package com.vbarjovanu.workouttimer.helpers.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BytesFileWriter {
    /**
     * @param filePath file path and name where to save the content
     * @param content  file's content to be saved
     * @throws IOException when file can't be written
     */
    public void writeFile(String filePath, byte[] content) throws IOException {
        File file = new File(filePath);
        FolderHelper.mkdir(file.getParentFile().getAbsolutePath(), true);
        OutputStream os = new FileOutputStream(file);
        os.write(content);
        os.flush();
        os.close();
    }
}
