package org.neil.main.app;

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
            processedArgs.put("default", args[args.length - 1]);
        }
        return processedArgs;
    }
}
