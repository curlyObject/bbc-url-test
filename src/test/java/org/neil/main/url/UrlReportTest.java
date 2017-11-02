package org.neil.main.url;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the implementations fo the UrlTester
 */
public class UrlReportTest {

    @Test
    public void reportError() {

        final String json = new UrlErrorReport("url", "I am Error").toJson();

        assertThat(json).isEqualTo(
                "{\n" +
                        "  \"Url\": \"url\",\n" +
                        "  \"Error\": \"I am Error\"\n" +
                        "}\n"
        );
    }

    @Test
    public void reportUrl() {

        final String json = new UrlTestReport("url", 200, 12345L, "date").toJson();

        assertThat(json).isEqualTo(
                "{\n" +
                        "  \"Url\": \"url\",\n" +
                        "  \"Status_code\": 200,\n" +
                        "  \"Content_length\": 12345,\n" +
                        "  \"Date\": \"date\"\n" +
                        "}\n"
        );
    }

}
