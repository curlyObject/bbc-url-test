package org.neil.main.app;

import org.neil.main.report.StatusReport;
import org.neil.main.url.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.neil.main.util.ErrorOutput.logError;
import static org.neil.main.util.LogOutput.logOutput;

/**
 * Manages the orchestration of testing urls.
 */
public class UrlTesterApplication {

    private static final String LINE_ENDING = "\n";

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
            outputReports(new ThreadedUrlTester().test(splitUrls(arguments.get("default")), new SimpleUrlVerifier()));
            successful = true;
        }
        return successful;
    }

    private List<String> splitUrls(String urlBlock) {

        return Arrays.asList(urlBlock.split(LINE_ENDING));
    }

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
