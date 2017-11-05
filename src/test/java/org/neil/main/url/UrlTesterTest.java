package org.neil.main.url;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Tests for the UrlTester implementations and building reports.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({URL.class, URLConnection.class, GetRequest.class})
public class UrlTesterTest {

    @Mock
    private UrlBuilder urlBuilder;

    private UrlTester urlTester = new ThreadedUrlTester();

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setUp() {

        reset(urlBuilder);
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void tearDown() {

        System.setErr(null);
    }

    @Test
    public void urlReport_WhenRequestComplete() throws Exception {

        final String url = "http://www.bbc.co.uk";
        final UrlTestReport expectedReport = new UrlTestReport(url, 200, 12345L, "date");
        final URL mockUrl = PowerMockito.mock(URL.class);
        final HttpURLConnection mockHttpConnection = PowerMockito.mock(HttpURLConnection.class);
        final Map<String, List<String>> mockHeaders = new HashMap<>();
        mockHeaders.put("Content-Length", Collections.singletonList("12345"));
        mockHeaders.put("Date", Collections.singletonList("date"));

        when(urlBuilder.verify(url)).thenReturn(true);
        when(urlBuilder.build(url)).thenReturn(mockUrl);
        PowerMockito.when(mockUrl.getProtocol()).thenReturn("http");
        PowerMockito.when(mockUrl.openConnection()).thenReturn(mockHttpConnection);
        PowerMockito.when(mockHttpConnection.getResponseCode()).thenReturn(200);
        PowerMockito.when(mockHttpConnection.getHeaderFields()).thenReturn(mockHeaders);

        final List<UrlReport> urlReports = urlTester.test(singletonList(url), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports).containsExactly(expectedReport));
    }

    @Test
    public void urlReport_WhenHttpsRequestComplete() throws Exception {

        final String url = "https://www.bbc.co.uk";
        final UrlTestReport expectedReport = new UrlTestReport(url, 200, 12345L, "date");
        final URL mockUrl = PowerMockito.mock(URL.class);
        final HttpURLConnection mockHttpConnection = PowerMockito.mock(HttpsURLConnection.class);
        final Map<String, List<String>> mockHeaders = new HashMap<>();
        mockHeaders.put("Content-Length", Collections.singletonList("12345"));
        mockHeaders.put("Date", Collections.singletonList("date"));

        when(urlBuilder.verify(url)).thenReturn(true);
        when(urlBuilder.build(url)).thenReturn(mockUrl);
        PowerMockito.when(mockUrl.getProtocol()).thenReturn("https");
        PowerMockito.when(mockUrl.openConnection()).thenReturn(mockHttpConnection);
        PowerMockito.when(mockHttpConnection.getResponseCode()).thenReturn(200);
        PowerMockito.when(mockHttpConnection.getHeaderFields()).thenReturn(mockHeaders);

        final List<UrlReport> urlReports = urlTester.test(singletonList(url), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports).containsExactly(expectedReport));
    }

    @Test
    public void errorReport_WhenRequestFails() throws Exception {

        final String url = "http://bogus.co";
        final UrlErrorReport expectedReport = new UrlErrorReport(url, "Url could not be connected to");
        when(urlBuilder.verify(url)).thenReturn(true);

        final URL mockUrl = PowerMockito.mock(URL.class);

        when(urlBuilder.verify(url)).thenReturn(true);
        when(urlBuilder.build(url)).thenReturn(mockUrl);
        PowerMockito.when(mockUrl.getProtocol()).thenReturn("http");
        PowerMockito.when(mockUrl.openConnection()).thenThrow(new IOException("I am Error"));
        final List<UrlReport> urlReports = urlTester.test(singletonList(url), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports).containsExactly(expectedReport));
    }

    @Test
    public void errorReport_WhenMalformedUrl() throws Exception {

        final String url = "http://bogus.co";
        final UrlErrorReport expectedReport = new UrlErrorReport(url, "URL Malformed");
        when(urlBuilder.verify(url)).thenReturn(true);
        when(urlBuilder.build(url)).thenThrow(new MalformedURLException());

        final List<UrlReport> urlReports = urlTester.test(singletonList(url), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports).containsExactly(expectedReport));
    }

    @Test
    public void errorReport_WhenUrlVerifyFail() throws Exception {

        final String url = "http://bogus.co";
        final UrlErrorReport expectedReport = new UrlErrorReport(url, "URL Malformed");
        when(urlBuilder.verify(url)).thenReturn(false);

        final List<UrlReport> urlReports = urlTester.test(singletonList(url), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports).containsExactly(expectedReport));
    }

    @Test
    public void reportList_WhenMultipleUrls() throws Exception {

        final String url1 = "http://www.bbc.co.uk";
        final String url2 = "ftp://me";
        final String url3 = "https://www.bbc.co.uk/oauth#setup";
        final UrlTestReport expectedReportUrl1 = new UrlTestReport(url1, 200, 12345L, "date");
        final UrlErrorReport expectedReportUrl2 = new UrlErrorReport(url2, "URL Malformed");
        final UrlTestReport expectedReportUrl3 = new UrlTestReport(url3, 200, 12345L, "date");
        final URL mockUrl1 = PowerMockito.mock(URL.class);
        final URL mockUrl3 = PowerMockito.mock(URL.class);
        final HttpURLConnection mockHttpConnection = PowerMockito.mock(HttpURLConnection.class);
        final HttpURLConnection mockHttpsConnection = PowerMockito.mock(HttpsURLConnection.class);
        final Map<String, List<String>> mockHeaders = new HashMap<>();
        mockHeaders.put("Content-Length", Collections.singletonList("12345"));
        mockHeaders.put("Date", Collections.singletonList("date"));

        when(urlBuilder.verify(url1)).thenReturn(true);
        when(urlBuilder.build(url1)).thenReturn(mockUrl1);
        PowerMockito.when(mockUrl1.getProtocol()).thenReturn("http");
        PowerMockito.when(mockUrl1.openConnection()).thenReturn(mockHttpConnection);
        PowerMockito.when(mockHttpConnection.getResponseCode()).thenReturn(200);
        PowerMockito.when(mockHttpConnection.getHeaderFields()).thenReturn(mockHeaders);

        when(urlBuilder.verify(url2)).thenReturn(false);
        when(urlBuilder.verify(url3)).thenReturn(true);
        when(urlBuilder.build(url3)).thenReturn(mockUrl3);

        when(urlBuilder.verify(url3)).thenReturn(true);
        when(urlBuilder.build(url3)).thenReturn(mockUrl3);
        PowerMockito.when(mockUrl3.getProtocol()).thenReturn("https");
        PowerMockito.when(mockUrl3.openConnection()).thenReturn(mockHttpsConnection);
        PowerMockito.when(mockHttpsConnection.getResponseCode()).thenReturn(200);
        PowerMockito.when(mockHttpsConnection.getHeaderFields()).thenReturn(mockHeaders);

        final List<UrlReport> urlReports = urlTester.test(Arrays.asList(url1, url2, url3), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports)
                .containsExactly(expectedReportUrl1, expectedReportUrl2, expectedReportUrl3));
    }

}
