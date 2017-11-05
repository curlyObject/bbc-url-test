package org.neil.main.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple argument processor that will take the last argument to be provided. Ignores any flags and doesn't care about formatting
 */
public class SimpleArgumentProcessor implements ArgumentProcessor {

    private static final String DEFAULT = "default";

    @Override
    public Map<String, String> process(String... args) {

        final HashMap<String, String> processedArgs = new HashMap<>();
        if (args.length > 0) {
            extractFlagArgument("--help", "-h", "help", processedArgs, args);
            if (args.length > 1) {

                extractFlagArgumentWithValue("--timeout", "-t", "timeout", processedArgs, args);
            }
            processedArgs.put("default", args[args.length - 1]);
        }
        return processedArgs;
    }

    private void extractFlagArgumentWithValue(String fullFlag, String shortFlag, String argumentName,
                                              Map<String, String> processedArgs, String... args) {

        final int fullFlagArgIndex = Arrays.binarySearch(args, fullFlag);
        // Try and extract the timeout flag and value
        if (fullFlagArgIndex >= 0) {
            processedArgs.put(argumentName, args[fullFlagArgIndex + 1]);
        } else {
            final int shortFlagArgIndex = Arrays.binarySearch(args, shortFlag);
            if (shortFlagArgIndex >= 0) {
                processedArgs.put(argumentName, args[shortFlagArgIndex + 1]);
            }
        }
    }

    private void extractFlagArgument(String fullFlag, String shortFlag, String argumentName,
                                     Map<String, String> processedArgs, String... args) {

        final int fullFlagArgIndex = Arrays.binarySearch(args, fullFlag);
        // Try and extract the timeout flag and value
        if (fullFlagArgIndex >= 0) {
            processedArgs.put(argumentName, null);
        } else {
            final int shortFlagArgIndex = Arrays.binarySearch(args, shortFlag);
            if (shortFlagArgIndex >= 0) {
                processedArgs.put(argumentName, null);
            }
        }
    }
}
