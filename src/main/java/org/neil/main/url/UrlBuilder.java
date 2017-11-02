package org.neil.main.url;

import java.net.URL;
import java.util.List;

/**
 * Functionality to verify and build a URL from a String.
 */
public interface UrlBuilder {

    /**
     * Will produce a URL object from a url String. It is exceptional behaviour if a poorly defined url string is provided.
     *
     * @param urlString The string representation of a Url to return
     * @return The string as Java URL object
     */
    URL build(String urlString);

    /**
     * Will verify a string to see if it is a correctly formatted URL. A List of errors can be produced of all of the syntax failures.
     *
     * @param urlString The string representing a Url
     * @return The List of all errors, empty if it is a valid url.
     */
    List<String> verify(String urlString);

}
