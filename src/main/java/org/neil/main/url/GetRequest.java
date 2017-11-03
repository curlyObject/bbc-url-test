package org.neil.main.url;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Optional;

import static org.neil.main.util.ErrorOutput.logError;

/**
 * Logic for performing a HTTP GET on the provided URL.
 * Package private as its only intended to be used by the UrlTester
 */
class GetRequest {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    /**
     * Perform a GET request on the provided URL. Handles the logic for the protocols HTTP and HTTPS.
     *
     * @param url The URL object to perform the GET request for.
     * @return The response for the request inside an optional or an empty optional if the request fails.
     */
    Optional<HttpResponse> getUrl(URL url) throws MalformedURLException {

        try {
            if (url.getProtocol().equals(HTTP) || url.getProtocol().equals(HTTPS)) {
                final URLConnection urlConnection = url.openConnection();
                if (urlConnection instanceof HttpURLConnection) {
                    try (GetHttpConnection getHttpConnection = new GetHttpConnection(
                            (HttpURLConnection) urlConnection)) {
                        return Optional.of(getHttpConnection.getResponse());
                    }
                }
            }
        } catch (IOException ioException) {
            logError(ioException.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Closable class to handle disconnecting
     */
    private class GetHttpConnection implements AutoCloseable {

        private static final String HTTP_GET = "GET";
        private static final int CONNECT_TIMEOUT = 10000;
        private static final int READ_TIMEOUT = 10000;

        private HttpURLConnection httpURLConnection;

        GetHttpConnection(HttpURLConnection httpURLConnection) {

            Objects.requireNonNull(httpURLConnection);
            this.httpURLConnection = httpURLConnection;
        }

        HttpResponse getResponse() throws IOException {

            httpURLConnection.setConnectTimeout(CONNECT_TIMEOUT);
            httpURLConnection.setReadTimeout(READ_TIMEOUT);
            httpURLConnection.setRequestMethod(HTTP_GET);
            httpURLConnection.connect();
            return new HttpResponse(httpURLConnection.getHeaderFields(), httpURLConnection.getResponseCode());
        }

        @Override
        public void close() {

            httpURLConnection.disconnect();
        }
    }

}
