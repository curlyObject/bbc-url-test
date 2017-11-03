package org.neil.main.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

/**
 * Helper class to provide utilities with testing and mocking web requests.
 */
public final class HttpTestUtils {

    private static final String CONTENT_LENGTH = "Content-Length";

    private HttpTestUtils() {

    }

    public static WireMockServer startMockServer(String address) {

        WireMockServer wireMockServer = new WireMockServer(
                options().bindAddress("localhost").dynamicPort().dynamicHttpsPort());
        wireMockServer.start();
        return wireMockServer;
    }

    public static String buildExpectedHttpsUrl(WireMockServer wireMockServer, String serverAddress, String path) {

        return "https://" + serverAddress + ":" + wireMockServer.httpsPort() + path;
    }

    public static String buildExpectedHttpUrl(WireMockServer wireMockServer, String serverAddress, String path) {

        return "http://" + serverAddress + ":" + wireMockServer.port() + path;
    }

    public static HttpHeader buildContentLengthHeader(long length) {

        return new HttpHeader(CONTENT_LENGTH, Long.toString(length));
    }

    public static HttpHeader buildDateHeader(String date) {

        return new HttpHeader(CONTENT_LENGTH, date);
    }

    public static void stubUrl(WireMockServer wireMockServer, String expectedUrl, HttpHeaders expectedHeaders,
                               int statusCode) {

        wireMockServer.stubFor(get(urlEqualTo(expectedUrl))
                .willReturn(aResponse()
                        .withStatus(statusCode)
                        .withHeaders(expectedHeaders)));
    }

}
