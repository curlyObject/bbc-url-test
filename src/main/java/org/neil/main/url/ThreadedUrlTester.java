package org.neil.main.url;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.neil.main.util.ErrorOutput.logError;

/**
 * MultiThreaded tester for urls
 */
public class ThreadedUrlTester implements UrlTester {

    /**
     * Converts the URL string into a URL object and then performs the GET request.
     * Will collect response information and collate into UrlReport objects.
     * Uses the parallelStream method to multi thread the calls, minimising the effect a slow request or non
     * respondent endpoint has.
     *
     * @param urls       The list of url strings to perform GET requests on.
     * @param urlBuilder The UrlBuilder object instance to use for url string verification and conversion.
     * @param timeout    The read and connect timeout values for the connection.
     * @return List of reports for every url string provided in urls
     */
    @Override
    public List<UrlReport> test(List<String> urls, UrlBuilder urlBuilder, int timeout) {

        return urls
                .parallelStream()
                .map(urlString -> {
                    final Optional<URL> maybeUrl = buildUrl(urlString, urlBuilder);
                    if (!maybeUrl.isPresent()) {
                        return new UrlErrorReport(urlString, "URL Malformed");
                    }
                    return maybeUrl.flatMap(url -> getHttpResponse(url, timeout))
                            .map(httpResponse -> buildUrlTestReport(urlString, httpResponse))
                            .orElse(new UrlErrorReport(urlString, "Url could not be connected to"));
                })
                .collect(Collectors.toList());

    }

    /**
     * Convert a Http Response into a Url report
     *
     * @param urlString    The url that this report is about
     * @param httpResponse The Http response collected for this url
     * @return A UrlReport of the respsonse
     */
    private UrlReport buildUrlTestReport(String urlString, HttpResponse httpResponse) {

        final Long contentLength = httpResponse.geContentLengthHeader().map(Long::valueOf).orElse(null);
        return new UrlTestReport(
                urlString,
                httpResponse.getStatusCode(),
                contentLength,
                httpResponse.getDateHeader().orElse(null));
    }

    /**
     * Attempts to connect and retrieve response for the provided URL
     *
     * @param url     The url to connect to
     * @param timeout The read and connect timeout values for the connection.
     * @return An Optional of the Http responses if it could dbe connected ot or any empty optional if the connection failed.
     */
    private Optional<HttpResponse> getHttpResponse(URL url, int timeout) {

        return new GetRequest().getUrl(url, timeout);
    }

    /**
     * Verifies and converts a string representation of a url to a URL object.
     *
     * @param url        The string representing a url to verify and build
     * @param urlBuilder The object to use to build and verify the url.
     * @return An optional of the URL object for the provided String if it is a valid url, otherwise returns an empty Optional
     */
    private Optional<URL> buildUrl(String url, UrlBuilder urlBuilder) {

        if (urlBuilder.verify(url)) {
            try {
                return Optional.of(urlBuilder.build(url));
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }
        return Optional.empty();
    }

}
