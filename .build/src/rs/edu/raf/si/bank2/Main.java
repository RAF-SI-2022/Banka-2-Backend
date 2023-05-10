package rs.edu.raf.si.bank2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static rs.edu.raf.si.bank2.StreamGobbler.NUL;

/**
 * The build script for this project.
 */
public class Main {

    /**
     * Array of names of all microservices.
     */
    private static final String[] MICROSERVICES = new String[]{
            "main"
    };
    /**
     * Helper Docker services.
     */
    private static final String[] HELPER_SERVICES = new String[]{
            "mariadb",
            "flyway",
            "mongodb"
    };
    /**
     * Name of the Docker network.
     */
    private static final String NETWORK_NAME = "bank2_net";
    /**
     * Docker image repository ("harbor") URL.
     */
    private static final String HARBOR_URL = "harbor.k8s.elab.rs/banka-2/";
    /**
     * The root harbor server. Used for testing login credentials.
     */
    private static final String HARBOR_URL_SERVER = "harbor.k8s.elab.rs";
    /**
     * The executor service to run separate threads on.
     */
    private final ExecutorService executorService;
    /**
     * Helper for creating processes.
     */
    private final ProcessHelper processHelper;
    /**
     * For parsing command line arguments.
     */
    private final ArgParser argParser;
    /**
     * The timestamp when the script was started.
     */
    private final String startTimestamp;
    /**
     * The shell for this system. Passed by the run script.
     */
    private String shellCommand;
    /**
     * Shell start tokens.
     */
    private List<String> shellStartTokens;
    /**
     * Wrapper for logging to CLI.
     */
    private Logger logger;

    /**
     * Default constructor.
     *
     * @param args command line arguments
     */
    public Main(String[] args) {
        this.logger = new Logger();
        executorService = Executors.newCachedThreadPool();
        processHelper = new ProcessHelper(executorService, logger);
        argParser = new ArgParser(args);
        startTimestamp = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss")
                .format(Calendar.getInstance().getTime());

        int shellStartTokenCount = 0;
        if (argParser.getArg(1, "--shellCommand").size() != 1) {
            error("Bad shell command specified; only 1 parameter accepted, e" +
                    ".g. --shellCommand /bin/bash");
            return;
        }

        shellCommand = argParser.getArg(1, "--shellCommand").get(0);

        if (argParser.getArg(1, "--shellStartTokenCount").size() != 1) {
            error("Bad shell token count specified; only 1 parameter " +
                    "accepted, e.g. --shellStartTokenCount 1");
            return;
        }

        try {
            shellStartTokenCount = Integer.parseInt(
                    argParser.getArg(1, "--shellStartTokenCount")
                            .get(0));
        } catch (Exception e) {
            error(e);
            return;
        }

        if (argParser.getArg(shellStartTokenCount, "--shellStartTokens").size() != shellStartTokenCount) {
            shellStartTokens = null;
            error("Bad shell tokens specified; count of shell start tokens " +
                    "must be exactly the same as --shellStartTokenCount");
            return;
        }

        shellStartTokens = argParser.getArg(shellStartTokenCount,
                "--shellStartTokens");
    }

    public static void main(String[] args) {
        new Main(args).run();
    }

    /**
     * Runs the script.
     */
    public void run() {
        Runtime.getRuntime().addShutdownHook(
                new Thread(this::cleanup)
        );

        String command = argParser.command();

        switch (command.toLowerCase()) {
            case "dev" -> {
                dev();
            }
            case "test" -> {
                test();
            }
            case "dist" -> {
                dist();
            }
            case "stack" -> {
                stack();
            }
            case "reset" -> {
                reset();
            }
            case "stop" -> {
                stop();
            }
            case "devenv" -> {
                devenv();
            }
            default -> {
                logger.error("Undefined command: \"" + command + "\"");
            }
        }

        cleanup();
    }

    /**
     * Creates a command that starts a new shell with this command. Use for
     * ProcessBuilder.
     *
     * @param command command to run
     * @return complete shell command
     */
    private List<String> makeShellStartCommand(String... command) {
        List<String> res = new LinkedList<>();
        res.add(shellCommand);
        res.addAll(shellStartTokens);
        res.add(String.format("\"%s\"", String.join(" ", command)));
        return res;
    }

    /**
     * Fetches the output directory path.
     *
     * @return output directory path
     */
    private String getOutDir() {
        List<String> defaultDir = new ArrayList<>();
        defaultDir.add("./logs/");
        List<String> outArgs = argParser.getArg(
                1,
                defaultDir,
                "--dirOut",
                "-do"
        );
        String d = outArgs.get(0);
        if (!d.endsWith(File.separator)) {
            d = d + File.separator;
        }
        d += startTimestamp;
        File f = new File(d);
        if (f.exists() && f.isFile()) {
            error("Specified output directory is a file");
            return null;
        }

        if (f.exists() && f.isDirectory())
            return d;

        boolean success = f.mkdirs();
        if (!success) {
            error("Failed to create output directory");
            return null;
        }

        return d;
    }

    /**
     * Fetches the error directory path.
     *
     * @return error directory path
     */
    private String getErrDir() {
        List<String> defaultDir = new ArrayList<>();
        defaultDir.add("./logs/");
        List<String> outArgs = argParser.getArg(
                1,
                defaultDir,
                "--dirErr",
                "-de"
        );
        String d = outArgs.get(0);
        File f = new File(d);
        if (!d.endsWith(File.separator)) {
            d = d + File.separator;
        }
        d += startTimestamp;
        if (f.exists() && f.isFile()) {
            error("Specified output directory is a file");
            return null;
        }

        if (f.exists() && f.isDirectory())
            return d;

        boolean success = f.mkdirs();
        if (!success) {
            error("Failed to create output directory");
            return null;
        }

        return d;
    }

    /**
     * Checks whether the Docker daemon is running. Errors if not.
     */
    private void assertDockerDaemonRunning() {
        try {
            Process proc = processHelper.startProcessIgnoreOutput(
                    "docker", "stats", "--no-stream");
            if (proc.waitFor() == 0) {
                logger.pass("Docker daemon running");
                return;
            }
            error("Docker daemon not running. Start Docker and try again.");
        } catch (InterruptedException e) {
            error(e);
        }
    }

    /**
     * Creates/prepares the Docker network.
     */
    private void createDockerNetwork() {
        try {
            Process proc = processHelper.startProcessIgnoreOutput(
                    "docker", "network", "inspect", NETWORK_NAME
            );
            if (proc.waitFor() == 0) {
                logger.pass("Docker network " + NETWORK_NAME + " running");
                return;
            }
            StringBuilder err = new StringBuilder();
            proc = processHelper.startProcessWithOutputError(
                    new ProcessBuilder()
                            .command(
                                    "docker", "network", "create",
                                    "--driver", "bridge",
                                    NETWORK_NAME),
                    NUL,
                    (l) -> {
                        err.append(l).append("\n");
                    }
            );
            if (proc.waitFor() == 0) {
                logger.pass("Docker network " + NETWORK_NAME + " created");
                return;
            }
            error("Failed to create Docker network:\n" + err);
        } catch (InterruptedException e) {
            error(e);
        }
    }

    /**
     * Starts all helper services on Docker.
     */
    private void composeDockerHelperServices() {
        for (String hs : HELPER_SERVICES) {
            processHelper.startProcessIgnoreOutput(
                    "docker", "compose", "up",
                    "-d", hs);
        }
        this.logger.info("Started Docker helper services");
    }

    /**
     * Builds the Docker image for the requested microservice.
     *
     * @param microservice microservice name
     */
    private void buildDockerImage(String microservice) {
        try {
            if (new ProcessBuilder(
                    makeShellStartCommand(
                            "mvnw",
                            "spotless:apply")
            ).directory(new File(
                            System.getProperty("user.dir")
                                    + "/" + microservice))
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.INHERIT)
                    .start()
                    .waitFor() != 0) {
                error("Failed to run mvnw on \"" + microservice + "\"");
            }
            if (processHelper.startProcessRedirect(
                    "docker", "build",
                    "-t", microservice,
                    "-f", "./docker/" + microservice + ".Dockerfile",
                    "."
            ).waitFor() != 0) {
                error("Failed to build image \"" + microservice + "\"");
                return;
            }

            String remoteName = HARBOR_URL + microservice;
            if (processHelper.startProcessIgnoreOutput(
                    "docker", "tag",
                    microservice, remoteName
            ).waitFor() != 0) {
                error("Failed to tag image " + microservice
                        + " as " + remoteName);
                return;
            }

            logger.info("Built image " + microservice);
        } catch (InterruptedException | IOException e) {
            error(e);
        }
    }

    /**
     * Runs a service on Docker with the given Spring profile.
     *
     * @param microservice microservice name
     * @param entrypoint   command to be passed to bash when running
     * @return started process
     */
    private Process runDockerService(String microservice, String entrypoint) {
        Path out = Paths.get(String.format(
                "%s%s%s.out.log",
                getOutDir(),
                File.separator,
                microservice
        ));
        Path err = Paths.get(String.format(
                "%s%s%s.err.log",
                getErrDir(),
                File.separator,
                microservice
        ));

        try {
            return new ProcessBuilder(
                    makeShellStartCommand(
                            "docker", "run",
                            "--rm",
                            "--name", microservice,
                            "--network", NETWORK_NAME,
                            "--entrypoint=\"\"",
                            microservice,
                            "/bin/bash", "-c",
                            "\"" + entrypoint + "\"")
            )
                    .redirectOutput(new File(out.toFile().getAbsolutePath()))
                    .redirectError(new File(err.toFile().getAbsolutePath()))
                    .start();
        } catch (IOException e) {
            error(e);
            return null;
        }
    }

    /**
     * Checks whether the user is logged into the defined harbor.
     */
    private void assertLoggedInToDockerRegistry() {
        String dockerCfgPath = System.getProperty("user.home") +
                "/.docker/config.json";
        File cfg = new File(dockerCfgPath);
        if (!cfg.exists() || !cfg.isFile()) {
            logger.warn("Could not find ~/.docker/config.json file");
            return;
        }

        try {
            boolean readAuths = false;
            boolean readHarbor = false;

            BufferedReader br = new BufferedReader(new FileReader(cfg));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("\"auths\":")) {
                    readAuths = true;
                    continue;
                }
                if (line.contains(HARBOR_URL_SERVER)) {
                    readHarbor = true;
                    break;
                }
            }
            br.close();

            if (!readAuths || !readHarbor) {
                logger.warn("Could not find harbor \"" + HARBOR_URL_SERVER +
                        "\" in Docker config file. Check if you're logged in.");
            }
        } catch (Exception ignored) {
            ;
        }
    }

    /**
     * "Synchronizes" Docker images on the host/remote by checking whether
     * the image is present on host and tagging it with HARBOR_URL/imgName, or
     * pulling the image from HARBOR_URL/imgName and tagging it with imgName.
     *
     * @param imgName local Docker image name (no URL)
     */
    private void existsOrPullDockerImage(String imgName) {
        try {
            String remoteName = HARBOR_URL + imgName;
            Process proc = processHelper.startProcessIgnoreOutput(
                    "docker", "inspect", "--type=image",
                    remoteName, imgName
            );
            if (proc.waitFor() == 0) {
                logger.pass("Image " + imgName + " found locally");
                return;
            }

            assertLoggedInToDockerRegistry();

            // not available locally, get from harbor
            proc = processHelper.startProcessIgnoreOutput(
                    "docker", "image", "pull", remoteName
            );
            if (!proc.waitFor(5, TimeUnit.MINUTES) ||
                    proc.exitValue() != 0) {
                error("Failed to pull image " + remoteName);
            }

            logger.info("Pulled Docker image " + remoteName);

            proc = processHelper.startProcessIgnoreOutput(
                    "docker", "tag", remoteName, imgName
            );
            if (proc.waitFor() != 0) {
                error("Failed to tag Docker image " + remoteName);
            }

            logger.info("Tagged Docker image " + remoteName + " as " + imgName);
        } catch (InterruptedException e) {
            error(e);
        }
    }

    /**
     * Dev command
     */
    public void dev() {
        assertDockerDaemonRunning();

        stop(true);
        createDockerNetwork();

        boolean local = argParser.hasArg("-l", "--local");

        composeDockerHelperServices();

        // containerized
        if (!local) {

            for (String t : MICROSERVICES) {
                buildDockerImage(t);
            }

            String comm = "java -jar -Dspring.profiles.active=container,dev " +
                    "app.jar";

            for (String m : MICROSERVICES) {
                runDockerService(m, comm);
            }

            logger.info("Started microservices");
        } else {
            try {
                for (String m : MICROSERVICES) {
                    ProcessBuilder pb = new ProcessBuilder(
                            makeShellStartCommand(
                                    "mvnw",
                                    "spotless:apply",
                                    "clean",
                                    "compile",
                                    "exec:java")
                    )
                            .redirectOutput(new File(
                                    String.format(
                                            "%s%s%s.out.log",
                                            getOutDir(),
                                            File.separator,
                                            m
                                    )
                            ))
                            .redirectError(new File(
                                    String.format(
                                            "%s%s%s.err.log",
                                            getErrDir(),
                                            File.separator,
                                            m
                                    )
                            ))
                            .directory(new File(
                            System.getProperty("user.dir")
                                    + "/" + m));
                    pb.environment().put("MAVEN_OPTS", "-Dspring.profiles" +
                            ".active=local,dev");
                    pb.start();
                }
            } catch (IOException e) {
                error(e);
                return;
            }
        }

        existsOrPullDockerImage("frontend");

        processHelper.startProcessIgnoreOutput(
                "docker", "run", "--rm", "-d", "--expose", "80",
                "--publish", "80:80", "--name", "frontend", "frontend"
        );

        logger.info("Started dev stack");
    }

    /**
     * Test command
     */
    public void test() {
        this.logger.info("Running test...");
    }

    /**
     * Dist command
     */
    public void dist() {

    }

    /**
     * Restarts all auxiliary Docker services.
     */
    public void stack() {
        for (String name : HELPER_SERVICES) {
            processHelper.startProcessIgnoreOutput(
                    "docker", "compose", "restart", name);
        }

        this.logger.info("Helper services restarted");
    }

    /**
     * Removes and rebuilds all Docker auxiliary services. Use this command if
     * encountering errors in your build process.
     */
    public void reset() {
        processHelper.startProcessIgnoreOutput(
                "docker", "compose", "-v", "down");

        for (String name : HELPER_SERVICES) {
            processHelper.startProcessIgnoreOutput(
                    "docker", "compose", "up", "-d", name);
        }

        this.logger.info("Reset helper services");
    }

    /**
     * Stops auxiliary services or microservices if true.
     *
     * @param microservices whether to stop microservices or helper services
     */
    public void stop(boolean microservices) {
        if (microservices) {
            for (String name : MICROSERVICES) {
                processHelper.startProcessIgnoreOutput(
                        "docker", "stop", name);
            }
            processHelper.startProcessIgnoreOutput(
                    "docker", "stop", "frontend");

            this.logger.info("Stopped microservices");
            return;
        }

        // only kill helper services
        this.logger.info("Stopped helper services");
        processHelper.startProcessIgnoreOutput(
                "docker", "compose", "down");
    }

    /**
     * Stops auxiliary services or microservices if "--microservices" passed.
     */
    public void stop() {
        if (argParser.hasArg("--all")) {
            stop(true);
            stop(false);
            return;
        }
        stop(argParser.hasArg("--microservices"));
    }

    /**
     * Cleans all residue from running this script.
     */
    public void clean() {
        // TODO: implement
    }

    /**
     * Devenv command
     */
    public void devenv() {

    }

    /**
     * Cleans up all processes before shutting down.
     */
    public void cleanup() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Throws an error and exits.
     *
     * @param e error string
     */
    public void error(Exception e) {
        logger.error(e.getMessage());
        cleanup();
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