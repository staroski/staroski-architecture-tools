package br.com.staroski.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class helps to parse command line arguments.<br>
 * The arguments must be informed following the "<tt>name value</tt>" pattern.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class Arguments {

    private final Set<String> names;
    private final Map<String, String> values;

    /**
     * Crater a new {@link Arguments} object.
     * 
     * @param args     The command line arguments to be parsed.
     * @param required The name os required parameters.
     */
    public Arguments(String[] args, String... required) {
        if (required == null || required.length < 1) {
            throw new IllegalArgumentException("At least one required parameter need to be specified.");
        }
        this.names = new HashSet<>();
        for (String param : required) {
            this.names.add(param);
        }
        this.values = parse(args, this.names);
    }

    /**
     * Gets the value of the parameter specified by <tt>name</tt>.
     * 
     * @param name        THe name of the parameter.
     * @param validValues An optional list of valid values for the specified parameter.
     * @return The parameter value.
     * @throws IllegalArgumentException if the parameter name or it's value is invalid.
     */
    public String getArgument(String name, String... validValues) {
        // Check if the parameter is in the list of valid parameters
        if (!names.contains(name)) {
            throw new IllegalArgumentException("Invalid parameter: " + name);
        }
        String value = values.get(name);
        // If validValues is provided, check if the value is valid
        if (validValues.length > 0) {
            for (String validValue : validValues) {
                if (validValue.equals(value)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Invalid value for parameter: " + name + ". Valid values: " + String.join(", ", validValues));
        }
        return value;
    }

    private Map<String, String> parse(String[] args, Set<String> required) {
        int argsCount = args == null ? 0 : args.length;
        // Check if the number of arguments is even (parameter and value)
        if (argsCount < 2 || argsCount % 2 != 0) {
            throw new IllegalArgumentException("Invalid number of arguments.");
        }
        // Iterate over the arguments and store them in the map
        Map<String, String> values = new HashMap<>();
        for (int i = 0; i < argsCount; i += 2) {
            // Check if the parameter is in the list of valid parameters
            String name = args[i];
            if (required.contains(name)) {
                String value = args[i + 1];
                values.put(name, value);
            } else {
                throw new IllegalArgumentException("Invalid parameter: " + name);
            }
        }
        return values;
    }
}