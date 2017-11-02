package org.neil.main.url;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for the implementation of the UrlBuilder interface
 *
 * Checks that that Urls are verified correctly and will build a Url
 */
public class UrlBuilderVerificationTest {

    private UrlBuilder urlBuilder;

    // ####################### POSITIVE TESTS #######################

    @Test
    public void returnTrue_WhenNoPath() {

        final String url = "http://www.bbc.co.uk";

        assertTrue(urlBuilder.verify(url));
    }

    @Test
    public void returnTrue_WhenWithPath() {

        final String url = "http://www.bbc.co.uk/some/thing";

        assertTrue(urlBuilder.verify(url));
    }

    @Test
    public void returnTrue_WhenWithQuery() {

        final String url = "http://www.bbc.co.uk?foo=bar";

        assertTrue(urlBuilder.verify(url));
    }

    @Test
    public void returnTrue_WhenWithMultipleQueries() {

        final String url = "http://www.bbc.co.uk?foo=bar&hope";

        assertTrue(urlBuilder.verify(url));
    }

    @Test
    public void returnTrue_WhenWithConvertedSpace() {

        final String url = "http://www.bbc.co.uk/foo+bar";

        assertTrue(urlBuilder.verify(url));
    }

    @Test
    public void returnTrue_WhenWithFragment() {

        final String url = "http://www.bbc.co.uk/foo#bar";

        assertTrue(urlBuilder.verify(url));
    }

    @Test
    public void returnTrue_WhenWithEscapedCharacter() {

        final String url = "http://www.bbc.co.uk/foo#bar?hope=%25";

        assertTrue(urlBuilder.verify(url));
    }

    @Test
    public void returnTrue_WhenWithLocalhostAndPort() {

        final String url = "http://localhost:8080";

        assertTrue(urlBuilder.verify(url));
    }

    // ################# NEGATIVE TESTS #########################

    @Test
    public void returnFalse_WhenNoProtocol() {

        final String url = "www.bbc.co.uk";

        assertFalse(urlBuilder.verify(url));
    }

    @Test
    public void returnFalse_WhenUnsupportedProtocol() {

        final String url = "ftp://www.bbc.co.uk";

        assertFalse(urlBuilder.verify(url));
    }

    @Test
    public void returnFalse_WhenUnescapedIllegalCharacter() {

        final String url = "http://www.bbc.co.uk//";

        assertFalse(urlBuilder.verify(url));
    }

}
