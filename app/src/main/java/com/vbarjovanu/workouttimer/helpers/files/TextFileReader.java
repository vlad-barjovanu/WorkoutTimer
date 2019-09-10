package com.vbarjovanu.workouttimer.helpers.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextFileReader {
    public StringBuilder readFile(String filePath) throws IOException {
        File file = new File(filePath);
        StringBuilder text = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            if(text.length()>0) {
                text.append('\n');
            }
            text.append(line);
        }
        br.close();

        return text;
    }
}
