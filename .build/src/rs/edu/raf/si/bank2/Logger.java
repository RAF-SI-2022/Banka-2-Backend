package rs.edu.raf.si.bank2;

import java.io.PrintWriter;

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

    private final PrintWriter out;
    private final PrintWriter err;

    /**
     * Default constructor.
     */
    public Logger() {
        out = new PrintWriter(System.out, true);
        err = new PrintWriter(System.err, true);
    }

    /**
     * Prints a message to the CLI.
     *
     * @param s message content
     */
    public void info(String s) {
        out.println(ANSI_GREEN + "[RUN] " + s + ANSI_RESET);
    }

    /**
     * Prints a "passing" message to the CLI.
     *
     * @param s message content
     */
    public void pass(String s) {
        out.println(ANSI_GREEN + "[RUN] " + s + ANSI_RESET);
    }

    /**
     * Prints a warning message to the CLI.
     * @param s message content
     */
    public void warn(String s) {
        out.println(ANSI_YELLOW + "[RUN] " + s + ANSI_RESET);
    }

    /**
     * Prints an error to the CLI.
     *
     * @param s error string
     */
    public void error(String s) {
        err.println(ANSI_RED + "[RUN] " + s + ANSI_RESET);
    }

    /**
     * Prints an error to the CLI.
     *
     * @param e exception
     */
    public void error(Exception e) {
        err.print(ANSI_RED);
        e.printStackTrace();
        err.print(ANSI_RESET);
    }
}
