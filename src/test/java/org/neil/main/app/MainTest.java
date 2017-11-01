package org.neil.main.app;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Set of End to End tests that initialises the whole application. The url's provided all
 * point to a Mock server with pre-loaded responses removing the dependencies on the internet.
 *
 * Black box tests
 */
public class MainTest {

    private static final String CONTENT_LENGTH = "Content-Length";
    private final String lineEnding = System.lineSeparator();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    // Sets up and tears down the mock server with random available ports per test case.
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

    @Before
    public void setUpStreams() {

        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanUpStreams() {

        System.setOut(null);
    }

    @Test
    public void statusDocument_WhenUrlFound() {

        ZonedDateTime expectedDate = ZonedDateTime.now();

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(12345L),
                buildDateHeader(expectedDate));

        stubFor(get(urlEqualTo("/my/resource"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeaders(expectedHeaders)));

        Main.main("http://www.bbc.co.uk/");

        final String output = outContent.toString();
        final String[] outputLines = output.split(lineEnding);

        assertThat(outputLines)
                .hasSize(6)
                .containsExactly(
                        "{",
                        "\"Url\": \"http://www.bbc.co.uk/iplayer\",",
                        "\"Status_code\":200,",
                        "\"Content_length\":209127,",
                        "\"Date\":\"Tue, 25 Jul 2017 17:00:55 GMT\"",
                        "}"
                );

    }

    @Test
    public void statusDocument_WhenUrlRedirects() {

        Main.main("https://google.com");
    }

    @Test
    public void statusDocument_WhenUrlModePermanently() {

        Main.main("http://bbc.co.uk/");
    }

    @Test
    public void statusDocument_WhenUrlNotModified() {

        Main.main("http://bbc.co.uk/kitten/pics.jpg");
    }

    @Test
    public void statusDocument_WhenUrlDoesNotResolve() {

        Main.main("http://not.exists.bbc.co.uk/");
    }

    @Test
    public void statusDocument_WhenUrlPageNotFound() {

        Main.main("http://www.bbc.co.uk/missing/thing");
    }

    @Test
    public void errorReport_WhenProtocolNotSupported() {

        Main.main("ftp://who/");
    }

    @Test
    public void errorReport_WhenUrlInvalid() {

        Main.main("foo/bar");
    }

    @Test
    public void errorReport_WhenResponseMalformed() {

        Main.main("foo/bar");
    }

    @Test
    public void errorReport_WhenResponseRandom() {

        Main.main("foo/bar");
    }

    @Test
    public void errorReport_WhenRequestTimesOut() {

        Main.main("foo/bar");
    }

    @Test
    public void errorReport_WhenConnectionReset() {

        Main.main("foo/bar");
    }

    @Test
    public void errorReport_WhenResponseEmpty() {

        Main.main("foo/bar");
    }

    private HttpHeader buildContentLengthHeader(long length) {

        return new HttpHeader(CONTENT_LENGTH, Long.toString(length));
    }

    // Conforms to the standard defined in RFC7231 for date formatting https://tools.ietf.org/html/rfc7231#section-7.1.1.1
    private HttpHeader buildDateHeader(ZonedDateTime date) {

        return new HttpHeader(CONTENT_LENGTH, DateTimeFormatter.RFC_1123_DATE_TIME.format(date));
    }

}
