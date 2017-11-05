package org.neil.main.url;

/**
 * Interfaces defining that an object can be printed as a json String
 */
public interface UrlReport {

    int getStatusCode();

    /**
     * Converts this object into a json string as the implementing class sees fit.
     *
     * @return The json string report contained within this object.
     */
    String toJson();

}
