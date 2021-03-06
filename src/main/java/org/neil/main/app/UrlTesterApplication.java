package org.neil.main.app;

import org.neil.main.report.StatusReport;
import org.neil.main.url.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.neil.main.util.ErrorOutput.logError;
import static org.neil.main.util.LogOutput.logOutput;

/**
 * Manages the orchestration of testing urls.
 */
public class UrlTesterApplication {

    private static final Pattern LINE_END_REGEX = Pattern.compile("(\\\\n)|\n");
    // 10 second timeout on requests
    private static final int TIMEOUT = 10000;
    private static final String HELP = "help";
    private static final String TIMEOUT_ARGUMENT = "timeout";
    private static final String DEFAULT = "default";

    /**
     * Tests urls to see if they can be reached and outputs a report for each url.
     *
     * @param arguments The arguments to define the behaviour of the application
     * @return Trues if the application ran successfully, false if no arguments are provided
     */
    public boolean testUrls(Map<String, String> arguments) {

        boolean successful = false;
        if (arguments.isEmpty()) {
            logError("Require an argument of urls, separated by new lines");
        } else {
            if (arguments.containsKey(HELP)) {
                logOutput(
                        "java -jar bbc-url-tester-1.0.jar <-h | --help> <-t | --timeout [integer value]> <urls>\n" +
                                "-h | --help   prints help message \n" +
                                "-t | --timeout   Set a timeout in milliseconds for connecting and reading urls provided\n" +
                                "urls   A new line separated list of urls to test");
                successful = true;
            } else {
                outputReports(new ThreadedUrlTester().test(splitUrls(arguments.get(DEFAULT)), new SimpleUrlVerifier(),
                        getTimeout(arguments)));
                successful = true;
            }
        }
        return successful;
    }

    /**
     * Splits the string on line end characters or by the escaped line end character
     * literals if thew string didn't split by the unescaped Line end character.
     *
     * @param urlBlock The String of line end separated urls.
     * @return An a array of urls split out from the provided url block string.
     */
    List<String> splitUrls(String urlBlock) {

        return Arrays.asList(LINE_END_REGEX.split(urlBlock));
    }

    private int getTimeout(Map<String, String> arguments) {

        if (arguments.containsKey(TIMEOUT_ARGUMENT)) {
            try {
                return Integer.valueOf(arguments.get(TIMEOUT_ARGUMENT));
            } catch (NumberFormatException ex) {
                logError("Timeout not a number, defaulting to 10 seconds");
            }
        }
        return TIMEOUT;
    }

    /**
     * Print all of the url reports for the passed and failed urls.
     *
     * Will tally the totals seen for each status code and output a report of these tallies after the url reports.
     *
     * @param urlReports
     */
    private void outputReports(List<UrlReport> urlReports) {

        final StatusReport statusReport = new StatusReport();

        urlReports.forEach(urlReport -> {
            statusReport.incrementStatus(urlReport.getStatusCode());
            if (urlReport instanceof UrlTestReport) {
                logOutput(urlReport.toJson());
            } else if (urlReport instanceof UrlErrorReport) {
                logError(urlReport.toJson());
            }
        });

        logOutput(statusReport.toJson());

    }

}
