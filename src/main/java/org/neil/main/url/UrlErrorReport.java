package org.neil.main.url;

public class UrlErrorReport implements UrlReport {

    private final String url;
    private final String error;

    public UrlErrorReport(String url, String error) {

        this.url = url;
        this.error = error;
    }

    //TODO Setup Jackson to convery Object to json

    /**
     * Returns the url and error in a simple json object
     *
     * @return the url and error in a json object
     */
    @Override
    public String toJson() {

        return "{\n" +
                "  \"Url\": \"" + url + "\",\n" +
                "  \"Error\": \"" + error + "\"\n" +
                "}\n";
    }
}
