package com.vbarjovanu.workouttimer.helpers.files;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class BytesFileWriterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void writeFile() {
        BytesFileWriter bytesFileWriter = new BytesFileWriter();
        File tempFile = null;
        String filePath;
        byte[] content = new byte[]{1, 2, 3};

        try {
            tempFile = folder.newFile("file.bin");
            filePath = tempFile.getPath();
            bytesFileWriter.writeFile(filePath, content);
            File file = new File(filePath);
            Assert.assertTrue(file.exists());
            Assert.assertEquals(3, file.length());
        } catch (IOException e) {
            Assert.assertNull(e);
        }
    }
}