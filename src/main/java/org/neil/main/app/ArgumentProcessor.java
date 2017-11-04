package org.neil.main.app;

import java.util.Map;

/**
 * Handles input of arguments from the command line, creating a map of flags to parameters
 */
public interface ArgumentProcessor {

    /**
     * Takes in a variable length array of Strings and converts it to a Map of key value pairs.
     * The process for this is deferred to implementations of this interface.
     *
     * @param args The arguments to convert to a Key value map
     * @return A key value map of the arguments or an empty map if no arguments provided.
     */
    Map<String, String> process(String... args);

}
