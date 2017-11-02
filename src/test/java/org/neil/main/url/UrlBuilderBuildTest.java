package org.neil.main.url;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Test for the implementation of the UrlBuilder interface
 *
 * Checks that that Urls are verified correctly and will build a Url
 */
public class UrlBuilderBuildTest {

    private UrlBuilder urlBuilder = new SimpleUrlVerifier();

    // ####################### POSITIVE TESTS #######################

    @Test
    public void returnUrl_WhenNoPath() throws Exception {

        final String url = "http://www.bbc.co.uk";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("www.bbc.co.uk");
            softly.assertThat(builtUrl.getPath()).as("Path").isEmpty();
            softly.assertThat(builtUrl.getQuery()).as("Query").isNull();
            softly.assertThat(builtUrl.getRef()).as("Fragment").isNull();
        });
    }

    @Test
    public void returnUrl_WhenWithPath() throws Exception {

        final String url = "http://www.bbc.co.uk/some/thing";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("www.bbc.co.uk");
            softly.assertThat(builtUrl.getPath()).as("Path").isEqualTo("/some/thing");
            softly.assertThat(builtUrl.getQuery()).as("Query").isNull();
            softly.assertThat(builtUrl.getRef()).as("Fragment").isNull();
        });
    }

    @Test
    public void returnUrl_WhenWithQuery() throws Exception {

        final String url = "http://www.bbc.co.uk?foo=bar";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("www.bbc.co.uk");
            softly.assertThat(builtUrl.getPath()).as("Path").isEmpty();
            softly.assertThat(builtUrl.getQuery()).as("Query").isEqualTo("foo=bar");
            softly.assertThat(builtUrl.getRef()).as("Fragment").isNull();
        });
    }

    @Test
    public void returnUrl_WhenWithMultipleQueries() throws Exception {

        final String url = "http://www.bbc.co.uk?foo=bar&hope";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("www.bbc.co.uk");
            softly.assertThat(builtUrl.getPath()).as("Path").isEmpty();
            softly.assertThat(builtUrl.getQuery()).as("Query").isEqualTo("foo=bar&hope");
            softly.assertThat(builtUrl.getRef()).as("Fragment").isNull();
        });
    }

    @Test
    public void returnUrl_WhenWithConvertedSpace() throws Exception {

        final String url = "http://www.bbc.co.uk/foo+bar";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("www.bbc.co.uk");
            softly.assertThat(builtUrl.getPath()).as("Path").isEqualTo("/foo+bar");
            softly.assertThat(builtUrl.getQuery()).as("Query").isNull();
            softly.assertThat(builtUrl.getRef()).as("Fragment").isNull();
        });
    }

    @Test
    public void returnUrl_WhenWithFragment() throws Exception {

        final String url = "http://www.bbc.co.uk/foo#bar";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("www.bbc.co.uk");
            softly.assertThat(builtUrl.getPath()).as("Path").isEqualTo("/foo");
            softly.assertThat(builtUrl.getQuery()).as("Query").isNull();
            softly.assertThat(builtUrl.getRef()).as("Fragment").isEqualTo("bar");
        });
    }

    @Test
    public void returnUrl_WhenWithEscapedCharacter() throws Exception {

        final String url = "http://www.bbc.co.uk/foo+bar?hope=%25";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("www.bbc.co.uk");
            softly.assertThat(builtUrl.getPath()).as("Path").isEqualTo("/foo+bar");
            softly.assertThat(builtUrl.getQuery()).as("Query").isEqualTo("hope=%25");
            softly.assertThat(builtUrl.getRef()).as("Fragment").isNull();
        });
    }

    @Test
    public void returnUrl_WhenWithLocalhostAndPort() throws Exception {

        final String url = "http://localhost:8080";

        final URL builtUrl = urlBuilder.build(url);

        assertSoftly(softly -> {
            softly.assertThat(builtUrl.getProtocol()).as("Protocol").isEqualTo("http");
            softly.assertThat(builtUrl.getAuthority()).as("Authority").isEqualTo("localhost:8080");
            softly.assertThat(builtUrl.getPath()).as("Path").isEmpty();
            softly.assertThat(builtUrl.getQuery()).as("Query").isNull();
            softly.assertThat(builtUrl.getRef()).as("Fragment").isNull();
        });
    }

    // ################# NEGATIVE TESTS #########################

    @Test(expected = MalformedURLException.class)
    public void exception_WhenNoProtocol() throws Exception {

        final String url = "www.bbc.co.uk";

        urlBuilder.build(url);
    }

    @Test(expected = MalformedURLException.class)
    public void exception_WhenUnsupportedProtocol() throws Exception {

        final String url = "ftp://www.bbc.co.uk";

        urlBuilder.build(url);
    }

    @Test(expected = MalformedURLException.class)
    public void exception_WhenUnknownProtocol() throws Exception {

        final String url = "zxy://www.bbc.co.uk";

        urlBuilder.build(url);
    }

    @Test(expected = MalformedURLException.class)
    public void exception_WhenUnescapedIllegalCharacter() throws Exception {

        final String url = "http://www.bbc.co.uk/[";

        urlBuilder.build(url);
    }

}
