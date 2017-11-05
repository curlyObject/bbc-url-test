package org.neil.main.util;

/**
 * Helper to output information to the standard output
 */
public final class LogOutput {

    private LogOutput() {

    }

    /**
     * Outputs the supplied string to the system standard output.
     * Appends a new line to the String.
     *
     * @param output The string to print to the standard output stream
     */
    public static void logOutput(String output) {

        System.out.println(output);
    }

}
