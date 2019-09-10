package com.vbarjovanu.workouttimer.helpers.files;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

public class TextFileReaderTest {

    @Test
    public void readFile() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        assert classLoader != null;
        URL resource = classLoader.getResource("helpers/files/TextFileReaderSample.txt");
        TextFileReader textFileReader = new TextFileReader();
        StringBuilder text = null;
        try {
            text = textFileReader.readFile(resource.getPath());
        } catch (IOException e) {
            Assert.assertNull(e);
        }
        Assert.assertNotNull(text);
        Assert.assertEquals(7, text.length());
        Assert.assertEquals("ABC", text.substring(0,3));
        Assert.assertEquals("DEF", text.substring(4,7));
    }
}