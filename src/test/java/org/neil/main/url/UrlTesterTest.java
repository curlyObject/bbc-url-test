package org.neil.main.url;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Tests for the UrlTester implementations and building reports.
 */
@RunWith(MockitoJUnitRunner.class)
public class UrlTesterTest {

    private UrlTester urlTester;

    @Mock
    private UrlBuilder urlBuilder;

    @Before
    public void setUp() {

        reset(urlBuilder);
    }

    @Test
    public void urlReport_WhenRequestComplete() throws Exception {

        final String url = "http://www.bbc.co.uk";
        final UrlTestReport expectedReport = new UrlTestReport(url, 200, 12345L, "date");
        when(urlBuilder.verify(url)).thenReturn(true);
        when(urlBuilder.build(url)).thenReturn(new URL(url));

        final List<UrlReport> urlReports = urlTester.test(singletonList(url), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports).containsExactly(expectedReport));
    }

    @Test
    public void errorReport_WhenRequestFails() throws Exception {

        final String url = "http://bogus.co";
        final UrlErrorReport expectedReport = new UrlErrorReport(url, "Request Failed");
        when(urlBuilder.verify(url)).thenReturn(true);
        when(urlBuilder.build(url)).thenReturn(new URL(url));

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
        final String url3 = "https://www.google.com/oauth#setup";
        final UrlTestReport expectedReportUrl1 = new UrlTestReport(url1, 200, 12345L, "date");
        final UrlErrorReport expectedReportUrl2 = new UrlErrorReport(url2, "URL Malformed");
        final UrlTestReport expectedReportUrl3 = new UrlTestReport(url3, 200, 12345L, "date");
        when(urlBuilder.verify(url1)).thenReturn(true);
        when(urlBuilder.build(url1)).thenReturn(new URL(url1));
        when(urlBuilder.verify(url2)).thenReturn(false);
        when(urlBuilder.verify(url3)).thenReturn(true);
        when(urlBuilder.build(url3)).thenReturn(new URL(url3));

        final List<UrlReport> urlReports = urlTester.test(Arrays.asList(url1, url2, url3), urlBuilder);

        assertSoftly(softly -> softly.assertThat(urlReports)
                .containsExactly(expectedReportUrl1, expectedReportUrl2, expectedReportUrl3));
    }



}
