package org.neil.main.util;

/**
 * Helper class for outputting errors
 */
public final class ErrorOutput {

    /**
     * Private constructor to prevent instantiation.
     */
    private ErrorOutput() {

    }

    /**
     * Outputs the supplied string to the system error stream.
     * Appends a new line to the String.
     *
     * @param error The string describing the error message
     */
    public static void logError(String error) {

        System.err.println(error);
    }

}
