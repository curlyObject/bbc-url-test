package org.neil.main.url;

import java.util.Objects;

public class UrlErrorReport implements UrlReport {

    private static final String LINE_ENDING = "\n";
    private final String url;
    private final String error;

    public UrlErrorReport(String url, String error) {

        this.url = url;
        this.error = error;
    }

    @Override
    public int getStatusCode() {

        return -1;
    }

    //TODO Setup Jackson to convert Object to json
    /**
     * Returns the url and error in a simple json object
     *
     * @return the url and error in a json object
     */
    @Override
    public String toJson() {

        return "{" + LINE_ENDING +
                "  \"Url\": \"" + url + "\"," + LINE_ENDING +
                "  \"Error\": \"" + error + "\"" + LINE_ENDING +
                "}" + LINE_ENDING;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UrlErrorReport that = (UrlErrorReport) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url, error);
    }

    @Override
    public String toString() {

        return "UrlErrorReport{" +
                "url='" + url + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
