package org.neil.main.url;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Simple verify that optimistically tries to place the string into a URL object
 */
public class SimpleUrlVerifier implements UrlBuilder {

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";

    @Override
    public URL build(String urlString) throws MalformedURLException {

        if (!verifyProtocol(urlString)) {
            throw new MalformedURLException("Protocol must be either http or https");
        }
        try {
            return new URI(urlString).toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    @Override
    public boolean verify(String urlString) {

        if (!verifyProtocol(urlString)) {
            return false;
        }
        try {
            final URI uri = new URI(urlString);
            if (!uri.isAbsolute()) {
                return false;
            }
            uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
        return true;
    }

    /**
     * Checks that the protocol is either http:// or https://
     *
     * @param urlString The url string to verify
     * @return true only if the string starts with either 'http://' or 'https://'
     */
    private boolean verifyProtocol(String urlString) {

        return urlString.startsWith(HTTP) || urlString.startsWith(HTTPS);
    }

}
