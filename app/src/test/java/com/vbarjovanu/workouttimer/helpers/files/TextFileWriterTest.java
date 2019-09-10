package com.vbarjovanu.workouttimer.helpers.files;

import android.content.Context;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TextFileWriterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void writeFile() {
        TextFileWriter textFileWriter = new TextFileWriter();
        String filePath = null;
        String content = "123";
        try {
            File tempFile = folder.newFile("file.txt");
            filePath = tempFile.getPath();
            textFileWriter.writeFile(filePath, content);
        } catch (IOException e) {
            Assert.assertNull(e);
        }

        TextFileReader textFileReader = new TextFileReader();
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = textFileReader.readFile(filePath);
        } catch (IOException e) {
            Assert.assertNull(e);
        }
        Assert.assertEquals(stringBuilder.substring(0),content);
    }
}