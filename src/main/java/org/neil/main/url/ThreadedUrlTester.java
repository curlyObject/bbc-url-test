package org.neil.main.url;

import java.net.MalformedURLException;
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
     * respondant endpoint has.
     *
     * @param urls       The list of url strings to perform GET requests on.
     * @param urlBuilder The UrlBuilder object instance to use for url string verification and conversion.
     * @return List of reports for every url string provided in urls
     */
    @Override
    public List<UrlReport> test(List<String> urls, UrlBuilder urlBuilder) {

        return urls
                .parallelStream()
                .map(urlString -> {
                    final Optional<URL> maybeUrl = buildUrl(urlString, urlBuilder);
                    if (!maybeUrl.isPresent()) {
                        return new UrlErrorReport(urlString, "URL Malformed");
                    }
                    return maybeUrl.flatMap(this::getHttpResponse)
                            .map(httpResponse -> buildUrlTestReport(urlString, httpResponse))
                            .orElse(new UrlErrorReport(urlString, "Url could not be connected to"));
                })
                .collect(Collectors.toList());

    }

    private UrlReport buildUrlTestReport(String urlString, HttpResponse httpResponse) {

        final Long contentLength = httpResponse.geContentLengthHeader().map(Long::valueOf).orElse(null);
        return new UrlTestReport(
                urlString,
                httpResponse.getStatusCode(),
                contentLength,
                httpResponse.getDateHeader().orElse(null));
    }

    private Optional<HttpResponse> getHttpResponse(URL url) {

        try {
            return new GetRequest().getUrl(url);
        } catch (MalformedURLException e) {
            return Optional.empty();
        }
    }

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
