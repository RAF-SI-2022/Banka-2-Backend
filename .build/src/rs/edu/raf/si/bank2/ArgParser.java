package rs.edu.raf.si.bank2;

import java.util.*;

/**
 * Helper class for parsing the CLI arguments.
 */
public class ArgParser {

    /**
     * Original args.
     */
    private final List<String> args;

    /**
     * Cached argument values.
     */
    private final Map<String, List<String>> argValuesCache = new HashMap<>();

    /**
     * Default constructor.
     *
     * @param args command line args
     */
    public ArgParser(String[] args) {
        this.args = Arrays.asList(args);
    }

    /**
     * Original arguments
     *
     * @return original arguments
     */
    public List<String> args() {
        return args;
    }

    /**
     * The command run by the user.
     *
     * @return command name
     */
    public String command() {
        if (args.size() == 0) {
            return "dev";
        }
        String c = args.get(0).toLowerCase();
        switch (c) {
            case "dev":
            case "test":
            case "dist":
            case "stack":
            case "reset":
            case "stop":
            case "help":
            case "clean":
            case "devenv":
                return c;
            default:
                return "dev";
        }
    }

    /**
     * Whether the command has an argument or not. Not case-sensitive.
     *
     * @param argAliases argument(s) to look for
     * @return true if any found, otherwise false
     */
    public boolean hasArg(String... argAliases) {
        if (args.size() == 0) return false;
        for (String i : args) {
            for (String j : argAliases) {
                if (i.equalsIgnoreCase(j))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns the value following the argument, if any. Returns default
     * value if not found.
     *
     * @param tokens       how many values to fetch
     * @param defaultValue default value to return if not found
     * @param argAliases   argument names (aliases)
     * @return argument value, if any
     */
    public List<String> getArg(
            int tokens,
            List<String> defaultValue,
            String... argAliases
    ) {
        List<String> result = new LinkedList<>();
        if (args.size() == 0) return defaultValue;
        boolean found = false;
        for (String alias : argAliases) {
            if (argValuesCache.containsKey(alias)) {
                return argValuesCache.get(alias);
            }
            int ind = args.indexOf(alias);
            if (ind < 0) continue;
            for (int i = 1; i < tokens + 1; i++) {
                if (ind + i >= args.size()) break;
                found = true;
                result.add(args.get(ind + i));
            }
        }
        if (!found || result.size() != tokens) {
            // copy found result if not found or invalid
            result = new LinkedList<>(defaultValue);
        }
        for (String alias : argAliases) {
            argValuesCache.put(alias, result);
        }
        return result;
    }

    /**
     * Returns the value following the argument, if any.
     *
     * @param tokens     how many values to fetch
     * @param argAliases argument names (aliases)
     * @return argument value, if any
     */
    public List<String> getArg(int tokens, String... argAliases) {
        return getArg(tokens, Collections.emptyList(), argAliases);
    }
}
