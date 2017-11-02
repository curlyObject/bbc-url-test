package org.neil.main.url;

public class UrlTestReport implements UrlReport {

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
    public String toJson() {

        return "{\n" +
                "  \"Url\": \"" + url + "\",\n" +
                "  \"Status_code\": " + statusCode + ",\n" +
                "  \"Content_length\": " + contentLength + ",\n" +
                "  \"Date\": \"" + date + "\"\n" +
                "}\n";
    }
}
