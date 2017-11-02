package org.neil.main.url;

import java.net.URL;

/**
 * Functionality to verify and build a URL from a String.
 */
public interface UrlBuilder {

    /**
     * Will produce a URL object from a url String.
     *
     * @param urlString The string representation of a Url to return
     * @return The string as Java URL object
     */
    URL build(String urlString);

    /**
     * Will verify a string to see if it is a correctly formatted URL. A List of errors can be produced of all of the syntax failures.
     *
     * @param urlString The string representing a Url
     * @return True only if the provided String represents a syntactically correct URL
     */
    boolean verify(String urlString);

}
