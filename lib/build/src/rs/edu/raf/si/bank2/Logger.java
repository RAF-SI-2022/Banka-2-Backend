package rs.edu.raf.si.bank2;

/**
 * For printing to the command line.
 */
public class Logger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * Default constructor.
     */
    public Logger() {

    }

    /**
     * Prints a message to the CLI.
     *
     * @param s message string
     */
    public void info(String s) {
        System.out.println(ANSI_GREEN + "[RUN] " + s + ANSI_RESET);
    }

    /**
     * Prints an error to the CLI.
     *
     * @param s error string
     */
    public void error(String s) {
        System.err.println(ANSI_RED + "[RUN] " + s + ANSI_RESET);
    }
}
