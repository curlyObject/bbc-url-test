package org.neil.main.url;

import java.util.Objects;

public class UrlTestReport implements UrlReport {

    private static final String LINE_ENDING = "\n";
    private final String url;
    private final int statusCode;
    // Might not be returned in the headers, will be null in this case
    private final Long contentLength;
    // Might not be returned in the headers, will be null in this case
    private final String date;

    public UrlTestReport(String url, int statusCode, Long contentLength, String date) {

        this.url = url;
        this.statusCode = statusCode;
        this.contentLength = contentLength;
        this.date = date;
    }

    @Override
    public int getStatusCode() {

        return statusCode;
    }

    @Override
    public String toJson() {

        return "{" + LINE_ENDING +
                "  \"Url\": \"" + url + "\"," + LINE_ENDING +
                "  \"Status_code\": " + statusCode + "," + LINE_ENDING +
                "  \"Content_length\": " + contentLength + "," + LINE_ENDING +
                "  \"Date\": \"" + date + "\"" + LINE_ENDING +
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
        UrlTestReport that = (UrlTestReport) o;
        return statusCode == that.statusCode &&
                Objects.equals(url, that.url) &&
                Objects.equals(contentLength, that.contentLength) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url, statusCode, contentLength, date);
    }

    @Override
    public String toString() {

        return "UrlTestReport{" +
                "url='" + url + '\'' +
                ", statusCode=" + statusCode +
                ", contentLength=" + contentLength +
                ", date='" + date + '\'' +
                '}';
    }
}
