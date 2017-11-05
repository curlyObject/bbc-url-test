package org.neil.main.app;

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

        return Arrays.asList(urlBlock.split("\n"));
    }

    private void outputReports(List<UrlReport> urlReports) {

        urlReports.forEach(urlReport -> {
            if (urlReport instanceof UrlTestReport) {
                logOutput(urlReport.toJson());
            } else if (urlReport instanceof UrlErrorReport) {
                logError(urlReport.toJson());
            }
        });
    }

}
