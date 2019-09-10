package com.vbarjovanu.workouttimer.helpers.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TextFileWriter {

    /**
     * @param filePath file path and name where to save the content
     * @param content file's content to be saved
     * @throws IOException
     */
    public void writeFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(content, 0, content.length());
        bw.flush();
        bw.close();
    }
}
