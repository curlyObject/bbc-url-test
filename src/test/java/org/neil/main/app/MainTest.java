package org.neil.main.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {

        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {

        System.setOut(null);
    }

    @Test
    public void getOutputWhenAPplicationRuns() {

        final String lineEnding = System.getProperty("line.separator");
        Main.main("test");
        assertEquals("Hello World!!!" + lineEnding + "test" + lineEnding, outContent.toString());
    }

}
