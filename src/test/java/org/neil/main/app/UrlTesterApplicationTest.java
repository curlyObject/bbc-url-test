package org.neil.main.app;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class UrlTesterApplicationTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private UrlTesterApplication urlTesterApplication = new UrlTesterApplication();

    @Before
    public void setUp() {

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() {

        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void errorMessage_WhenNoUrls() {

        urlTesterApplication.testUrls(new HashMap<>());

        assertThat(errContent.toString()).contains("Require an argument of urls, separated by new lines");

    }

    @Test
    public void multipleUrls_WhenLineEndSeparatedUrls() {

        final String urls = "https://www.bbc.co.uk\nhttps://www.google.co.uk";
        final List<String> urlList = urlTesterApplication.splitUrls(urls);

        assertSoftly(softly -> softly.assertThat(urlList)
                .containsExactly("https://www.bbc.co.uk", "https://www.google.co.uk"));

    }

    @Test
    public void multipleUrls_WhenEscapedLineEndSeparatedUrls() {

        final String urls = "https://www.bbc.co.uk\\nhttps://www.google.co.uk";
        final List<String> urlList = urlTesterApplication.splitUrls(urls);

        assertSoftly(softly -> softly.assertThat(urlList)
                .containsExactly("https://www.bbc.co.uk", "https://www.google.co.uk"));

    }

}
