package org.neil.main.url;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpResponse {

    private final Map<String, List<String>> headers;
    private final int statusCode;

    public HttpResponse(Map<String, List<String>> headers, int statusCode) {

        this.headers = headers;

        this.statusCode = statusCode;
    }

    public int getStatusCode() {

        return statusCode;
    }

    public Optional<String> getDateHeader() {

        final List<String> dateValues = headers.get("Date");
        if (Objects.nonNull(dateValues) && !dateValues.isEmpty()) {
            return Optional.of(dateValues.get(0));
        }
        return Optional.empty();
    }

    public Optional<String> geContentLengthHeader() {

        final List<String> contentLengthValues = headers.get("Content-Length");
        if (Objects.nonNull(contentLengthValues) && !contentLengthValues.isEmpty()) {
            return Optional.of(contentLengthValues.get(0));
        }
        return Optional.empty();
    }
}
