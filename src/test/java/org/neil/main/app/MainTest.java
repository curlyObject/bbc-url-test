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

    private WireMockServer wireMockServer;

    @Before
    public void setUp() {

        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {

        if (Objects.nonNull(wireMockServer)) {
            wireMockServer.stop();
        }
        System.setOut(null);
    }

    @Test
    public void statusDocument_WhenUrlFound() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlHttps() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpsUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlRedirects() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 308;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlModePermanently() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 301;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlNotModified() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 304;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());

    }

    @Test
    public void statusDocument_WhenUrlPageNotFound() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 404;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());
    }

    @Test
    public void statusDocument_WhenContentLengthMissing() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildDateHeader(expectedDate));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, null, extractStandardOutput());
    }

    @Test
    public void statusDocument_WhenDateMissing() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = null;
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertStatusDocument(expectedDate, expectedUrl, statusCode, LENGTH, extractStandardOutput());
    }

    @Test
    public void errorDocument_WhenUrlDoesNotResolve() {

        startMockServer(MOCK_SERVER);

        final String expectedDate = getDate();
        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, "/fake");
        final String expectedError = "URL could not be resolved by the DNS server";

        final HttpHeaders expectedHeaders = new HttpHeaders(buildContentLengthHeader(LENGTH),
                buildDateHeader(expectedDate));

        final int statusCode = 200;
        stubUrl(wireMockServer, TEST_PATH, expectedHeaders, statusCode);

        Main.main(expectedUrl);

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenProtocolNotSupported() {

        final String expectedUrl = "ftp://dr@who/test";
        final String expectedError = "Invalid Url";

        Main.main(expectedUrl);

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenProtocolMissing() {

        final String expectedUrl = "www.bbc.co.uk";
        final String expectedError = "Invalid Url";

        Main.main(expectedUrl);

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenResponseMalformed() {

        startMockServer(MOCK_SERVER);

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Response is Malformed";

        wireMockServer.stubFor(get(urlEqualTo(expectedUrl))
                .willReturn(aResponse()
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        Main.main(expectedUrl);

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenResponseRandom() {

        startMockServer(MOCK_SERVER);

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Response is Malformed";

        wireMockServer.stubFor(get(urlEqualTo(expectedUrl))
                .willReturn(aResponse()
                        .withFault(Fault.RANDOM_DATA_THEN_CLOSE)));

        Main.main(expectedUrl);

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenRequestTimesOut() {

        startMockServer(MOCK_SERVER);

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Request Timed Out";

        wireMockServer.stubFor(get(urlEqualTo(expectedUrl))
                .willReturn(aResponse()
                        .withFixedDelay(10000)));

        Main.main(expectedUrl);

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenConnectionReset() {

        startMockServer(MOCK_SERVER);

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Connection reset";

        wireMockServer.stubFor(get(urlEqualTo(expectedUrl))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        Main.main(expectedUrl);

        assertErrorDocument(expectedUrl, expectedError);
    }

    @Test
    public void errorDocument_WhenResponseEmpty() {

        startMockServer(MOCK_SERVER);

        final String expectedUrl = buildExpectedHttpUrl(wireMockServer, MOCK_SERVER, TEST_PATH);
        final String expectedError = "Connection reset";

        wireMockServer.stubFor(get(urlEqualTo(expectedUrl))
                .willReturn(aResponse()
                        .withFault(Fault.EMPTY_RESPONSE)));

        Main.main(expectedUrl);

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

    private void assertStatusDocument(String expectedDateFormatted, String expectedUrl, int statusCode,
                                      Long expectedLength, String[] outputLines) {

        assertThat(outputLines)
                .hasSize(6)
                .containsExactly(
                        "{",
                        "  \"Url\": \"" + expectedUrl + "\",",
                        "  \"Status_code\": " + statusCode + ",",
                        "  \"Content_length\": " + expectedLength + ",",
                        "  \"Date\": \"" + expectedDateFormatted + "\"",
                        "}\n"
                );
    }

    private void assertErrorDocument(String expectedUrl, String expectedError) {

        assertThat(extractStandardOutput())
                .hasSize(4)
                .containsExactly(
                        "{",
                        "  \"Url\": \"" + expectedUrl + "\",",
                        "  \"Error\": \"" + expectedError + "\",",
                        "}\n"
                );
    }

}
