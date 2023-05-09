package rs.edu.raf.si.bank2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The build script for this project.
 */
public class Main {

    /**
     * The executor service to run separate threads on.
     */
    private ExecutorService executorService;

    /**
     * Wrapper for logging to CLI.
     */
    private Logger logger = null;

    /**
     * Default constructor.
     */
    public Main() {
        this.logger = new Logger();
        executorService = Executors.newCachedThreadPool();
    }

    public static void main(String[] args) {
        new Main().run(args);
    }

    /**
     * Runs the script.
     *
     * @param args command line arguments
     */
    public void run(String[] args) {
        List<String> argsList = Arrays.asList(args);
        if (argsList.size() == 0) {
            dev(argsList);
            return;
        }

        switch (argsList.get(0).toLowerCase()) {
            case "dev" -> {
                dev(argsList);
            }
            case "test" -> {
                test(argsList);
            }
            case "dist" -> {
                dist(argsList);
            }
            case "stack" -> {
                stack(argsList);
            }
            case "reset" -> {
                reset(argsList);
            }
            case "stop" -> {
                stop(argsList);
            }
            default -> {
                logger.error("Undefined command: \"" + args[0] + "\"");
            }
        }

    }

    /**
     * Checks whether the Docker daemon is running. Errors if not.
     */
    public void checkDockerRunning() {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command("docker", "stats");
        try {
            Process proc = pb.start();
            executorService.submit(StreamGobbler.consumeOut(proc));
            executorService.submit(StreamGobbler.consumeErr(proc));
            if (proc.waitFor() != 0) {
                error("Docker daemon not running. Start Docker and try again.");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Dev command
     *
     * @param args command line arguments
     */
    public void dev(List<String> args) {
        checkDockerRunning();

        this.logger.info("Starting dev...");
        boolean local = false;
        for (String arg : args) {
            if (!arg.equals("--local"))
                continue;
            local = true;
            break;
        }

        if (!local) {
            return;
        }


    }

    /**
     * Test command
     *
     * @param args command line arguments
     */
    public void test(List<String> args) {
        this.logger.info("Running test...");
    }

    /**
     * Dist command
     *
     * @param args command line arguments
     */
    public void dist(List<String> args) {

    }

    /**
     * Stack command
     *
     * @param args command line arguments
     */
    public void stack(List<String> args) {

    }

    /**
     * Reset command
     *
     * @param args command line arguments
     */
    public void reset(List<String> args) {

    }

    /**
     * Stop command
     *
     * @param args command line arguments
     */
    public void stop(List<String> args) {

    }

    /**
     * Devenv command
     *
     * @param args command line arguments
     */
    public void devenv(List<String> args) {

    }

    /**
     * Throws an error and exits.
     *
     * @param e error string
     */
    public void error(Exception e) {
        logger.error(e.getMessage());
        System.exit(1);
    }

    /**
     * Throws an error and exits.
     *
     * @param s error string
     */
    public void error(String s) {
        logger.error(s);
        System.exit(1);
    }

}