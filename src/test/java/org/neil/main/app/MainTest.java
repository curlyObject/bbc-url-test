package org.neil.main.app;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.neil.main.utils.HttpTestUtils.*;

/**
 * Set of End to End tests that initialises the whole application. The url's provided all
 * point to a Mock server with pre-loaded responses removing the dependencies on the internet.
 *
 * Black box tests
 */
public class MainTest {

    private static final long LENGTH = 12345L;
    private static final String MOCK_SERVER = "localhost";
    private static final String TEST_PATH = "/test";
    private final String lineEnding = System.lineSeparator();

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private WireMockServer wireMockServer;

    @Before
    public void setUp() {

        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        wireMockServer = startMockServer(MOCK_SERVER);
    }

    @After
    public void tearDown() {

        if (Objects.nonNull(wireMockServer)) {
            wireMockServer.stop();
        }
        System.setOut(null);
        System.setErr(null);
    }

    @Test
    public void statusDocument_WhenUrlFound() {

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.executeUrlTester(new String[] {expectedUrl});

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenMultipleUrls() {

        final String expectedDate = getDate();
        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));
        final int statusCode = 200;

        final String expectedUrl1 = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, "/1");
        stubUrl(wireMockServer, "/1", expectedHeaders, statusCode);

        final String expectedUrl2 = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, "/2");
        wireMockServer.stubFor(get(urlEqualTo("/2"))
                .willReturn(aResponse()
                        .withFixedDelay(11000)));

        final String expectedUrl3 = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, "/3");
        stubUrl(wireMockServer, "/3", expectedHeaders, statusCode);

        final String expectedUrl4 = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, "/4");
        wireMockServer.stubFor(get(urlEqualTo("/4"))
                .willReturn(aResponse()
                        .withFault(Fault.EMPTY_RESPONSE)));

        final String expectedUrl5 = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, "/5");
        stubUrl(wireMockServer, "/5", expectedHeaders, statusCode);

        final String expectedUrl6 = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, "/6");
        stubUrl(wireMockServer, "/6", expectedHeaders, statusCode);

        final String expectedUrl7 = "fake.co";

        Main.executeUrlTester(
                new String[] {String.join("\n", expectedUrl1, expectedUrl2, expectedUrl3, expectedUrl4, expectedUrl5,
                        expectedUrl6, expectedUrl7)});

        assertThat(extractStandardOutput())
                .hasSize(5)
                .containsExactly(
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl1 + "\",\n" +
                                "  \"Status_code\": " + statusCode + ",\n" +
                                "  \"Content_length\": " + LENGTH + ",\n" +
                                "  \"Date\": \"" + expectedDate + "\"\n" +
                                "}\n",
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl3 + "\",\n" +
                                "  \"Status_code\": " + statusCode + ",\n" +
                                "  \"Content_length\": " + LENGTH + ",\n" +
                                "  \"Date\": \"" + expectedDate + "\"\n" +
                                "}\n",
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl5 + "\",\n" +
                                "  \"Status_code\": " + statusCode + ",\n" +
                                "  \"Content_length\": " + LENGTH + ",\n" +
                                "  \"Date\": \"" + expectedDate + "\"\n" +
                                "}\n",
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl6 + "\",\n" +
                                "  \"Status_code\": " + statusCode + ",\n" +
                                "  \"Content_length\": " + LENGTH + ",\n" +
                                "  \"Date\": \"" + expectedDate + "\"\n" +
                                "}\n",
                        "[\n" +
                                "  {\n" +
                                "    \"Status_code\": -1,\n" +
                                "    \"Number_of_responses\": 3\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"Status_code\": 200,\n" +
                                "    \"Number_of_responses\": 4\n" +
                                "  }\n" +
                                "]\n"
                );

        assertThat(extractStandardErr())
                .contains(
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl2 + "\",\n" +
                                "  \"Error\": \"Url could not be connected to\"\n" +
                                "}\n",
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl4 + "\",\n" +
                                "  \"Error\": \"Url could not be connected to\"\n" +
                                "}\n",
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl7 + "\",\n" +
                                "  \"Error\": \"URL Malformed\"\n" +
                                "}\n"

                );

    }

    @Test
    public void statusDocument_WhenUrlRedirects() {

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildDateHeader(expectedDate));

        final int statusCode = 308;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.executeUrlTester(new String[] {expectedUrl});

        assertStatusDocument(expectedDate, expectedUrl, statusCode, null, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlModePermanently() {

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildDateHeader(expectedDate));

        final int statusCode = 301;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.executeUrlTester(new String[] {expectedUrl});

        assertStatusDocument(expectedDate, expectedUrl, statusCode, null, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlNotModified() {

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildDateHeader(expectedDate));

        final int statusCode = 304;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.executeUrlTester(new String[] {expectedUrl});

        assertStatusDocument(expectedDate, expectedUrl, statusCode, null, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlPageNotFound() {

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildDateHeader(expectedDate));

        final int statusCode = 404;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.executeUrlTester(new String[] {expectedUrl});

        assertStatusDocument(expectedDate, expectedUrl, statusCode, null, extractStandardOutput());
    }

    @Test
    public void statusDocument_WhenContentLengthMissing() {

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildDateHeader(expectedDate));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.executeUrlTester(new String[] {expectedUrl});

        assertStatusDocument(expectedDate, expectedUrl, statusCode, null, extractStandardOutput());
    }

    @Test
    public void statusDocument_WhenDateMissing() {

        final String expectedDate = null;
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.executeUrlTester(new String[] {expectedUrl});

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());
    }

    @Test
    public void errorDocument_WhenProtocolNotSupported() {

        final String expectedUrl = "ftp://dr@who/test";
        final String expectedError = "URL Malformed";

        Main.executeUrlTester(new String[] {expectedUrl});

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenProtocolMissing() {

        final String expectedUrl = "www.bbc.co.uk";
        final String expectedError = "URL Malformed";

        Main.executeUrlTester(new String[] {expectedUrl});

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenRequestTimesOut() {

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Url could not be connected to";

        wireMockServer.stubFor(get(urlEqualTo(TEST_PATH))
                .willReturn(aResponse()
                        .withFixedDelay(11000)));

        Main.executeUrlTester(new String[] {expectedUrl});

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenConnectionReset() {

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Url could not be connected to";

        wireMockServer.stubFor(get(urlEqualTo(TEST_PATH))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        Main.executeUrlTester(new String[] {expectedUrl});

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenResponseEmpty() {

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Url could not be connected to";

        wireMockServer.stubFor(get(urlEqualTo(TEST_PATH))
                .willReturn(aResponse()
                        .withFault(Fault.EMPTY_RESPONSE)));

        Main.executeUrlTester(new String[] {expectedUrl});

        assertErrorDocument(expectedUrl, expectedError);
    }

    // Conforms to the standard defined in RFC7231 for date formatting https://tools.ietf.org/html/rfc7231#section-7.1.1.1
    private String getDate() {

        return DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
    }

    private String[] extractStandardOutput() {

        final String output = outContent.toString();
        return output.split(lineEnding);
    }

    private String[] extractStandardErr() {

        final String output = errContent.toString();
        return output.split(lineEnding);
    }

    private void assertStatusDocument(String expectedDateFormatted, String expectedUrl, int statusCode,
                                      Long expectedLength, String[] outputLines) {

        assertThat(outputLines)
                .hasSize(2)
                .containsExactly(
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl + "\",\n" +
                                "  \"Status_code\": " + statusCode + ",\n" +
                                "  \"Content_length\": " + expectedLength + ",\n" +
                                "  \"Date\": \"" + expectedDateFormatted + "\"\n" +
                                "}\n",
                        "[\n" +
                                "  {\n" +
                                "    \"Status_code\": " + statusCode + ",\n" +
                                "    \"Number_of_responses\": 1\n" +
                                "  }\n" +
                                "]\n"

                );
    }

    private void assertErrorDocument(String expectedUrl, String expectedError) {

        assertThat(extractStandardErr())
                .contains(
                        "{\n" +
                                "  \"Url\": \"" + expectedUrl + "\",\n" +
                                "  \"Error\": \"" + expectedError + "\"\n" +
                                "}\n"
                );
        assertThat(extractStandardOutput())
                .contains(
                        "[\n" +
                                "  {\n" +
                                "    \"Status_code\": -1,\n" +
                                "    \"Number_of_responses\": 1\n" +
                                "  }\n" +
                                "]\n"
                );
    }

}
