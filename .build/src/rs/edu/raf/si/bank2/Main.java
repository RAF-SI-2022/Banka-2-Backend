package rs.edu.raf.si.bank2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
     * Wrapper for logging to CLI.
     */
    private final Logger logger;
    /**
     * List of all started processes that need to be cleaned up.
     */
    private final Set<Process> startedProcesses;
    /**
     * The shell for this system. Passed by the run script.
     */
    private String shellCommand;
    /**
     * Shell start tokens.
     */
    private List<String> shellStartTokens;

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
        startTimestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                .format(Calendar.getInstance().getTime());
        startedProcesses = new HashSet<>();

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

    /**
     * Main
     *
     * @param args
     */
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
            case "clean" -> {
                clean();
                return;
            }
            case "help" -> {
                help();
                return;
            }
            case "devenv" -> {
                devenv();
            }
            default -> {
                logger.error("Undefined command: \"" + command + "\"");
            }
        }

        boolean allStopped = true;
        for (Process p : startedProcesses) {
            if (p.isAlive()) {
                allStopped = false;
                break;
            }
        }
        if (startedProcesses.isEmpty() || allStopped) {
            logger.info("Done");
            cleanup();
            return;
        }

        logger.info("Done. Press ENTER to exit and kill processes");

        AtomicBoolean kill = new AtomicBoolean(false);
        executorService.submit(() -> {
            Scanner sc = new Scanner(System.in);
            sc.nextLine();
            kill.set(true);
        });

        while (true) {
            if (kill.get()) {
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(300);
                allStopped = true;
                for (Process p : startedProcesses) {
                    if (p.isAlive()) {
                        allStopped = false;
                        break;
                    }
                }
                if (allStopped) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
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
     * Creates a command that starts a new shell with this command in the
     * specified directory. Use for ProcessBuilder.
     *
     * @param dir     where the command should be executed
     * @param command command to run
     * @return complete shell command
     */
    private List<String> makeShellStartCommand(File dir, String... command) {
        List<String> res = new LinkedList<>();
        res.add(shellCommand);
        res.addAll(shellStartTokens);
        res.add(String.format(
                "\"cd %s && %s\"", dir.getAbsolutePath(),
                String.join(" ", command)));
        return res;
    }

    /**
     * Returns the platform-dependent string for executing a local shell script.
     *
     * @param scriptFile path to the script
     * @return command string
     */
    private String runScriptShCmd(String scriptFile) {
        if (!argParser.hasArg("--platform")) {
            error("Platform not specified, fix run shell script");
            return null;
        }

        List<String> toks = argParser.getArg(1, "--platform");
        if (toks.size() < 1) {
            error("Platform not specified, fix run shell script");
            return null;
        }

        String platform = toks.get(0);

        if (platform.equalsIgnoreCase("windows")) {
            return scriptFile + ".cmd";
        }

        return scriptFile;
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
     * Creates/prepares the Docker network. Platform-dependent: "bridge" may
     * not be available on Windows. Try nat.
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
            proc = new ProcessBuilder(
                    "docker", "network", "create",
                    "--driver", "bridge",
                    NETWORK_NAME
            ).redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectError(ProcessBuilder.Redirect.DISCARD)
                    .start();

            // if bridge not available, try nat
            if (proc.waitFor() != 0) {
                proc = new ProcessBuilder(
                        "docker", "network", "create",
                        "--driver", "nat",
                        NETWORK_NAME
                ).redirectOutput(ProcessBuilder.Redirect.DISCARD)
                        .redirectError(ProcessBuilder.Redirect.DISCARD)
                        .start();
            }

            if (proc.waitFor() == 0) {
                logger.pass("Docker network " + NETWORK_NAME + " created");
                return;
            }

            error("Failed to create Docker network");
        } catch (InterruptedException | IOException e) {
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
            File rundir = new File(System.getProperty("user.dir")
                    + File.separator + microservice);
            if (new ProcessBuilder(
                    runScriptShCmd(rundir.getAbsolutePath() + File.separator + "mvnw"),
                    "spotless:apply"
            )
                    .directory(rundir)
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
     * Runs a service on Docker with the given Spring profile. Each started
     * process is added to {@link #startedProcesses}.
     *
     * @param microservice microservice name
     * @param entrypoint   command to be passed to bash when running
     * @return started process
     */
    private Process runDockerService(String microservice, String entrypoint) {
        return runDockerService(microservice, entrypoint, null, false);
    }

    /**
     * Runs a service on Docker with the given Spring profile. Each started
     * process is added to {@link #startedProcesses}.
     *
     * @param microservice microservice name
     * @param entrypoint   command to be passed to bash when running
     * @param environment  map of environment variables to be passed to the
     *                     service
     * @param inheritIO    whether to redirect IO to inherit or to use log files
     * @return started process
     */
    private Process runDockerService(
            String microservice,
            String entrypoint,
            Map<String, String> environment,
            boolean inheritIO
    ) {
        Path out = null, err = null;
        if (!inheritIO) {
            out = Paths.get(String.format(
                    "%s%s%s.out.log",
                    getOutDir(),
                    File.separator,
                    microservice
            ));
            err = Paths.get(String.format(
                    "%s%s%s.err.log",
                    getErrDir(),
                    File.separator,
                    microservice
            ));
        }

        try {
            List<String> command = new LinkedList<>();
            command.add("docker");
            command.add("run");
            command.add("--entrypoint");
            command.add("/bin/bash");
            command.add("--rm");
            if (environment != null) {
                for (Map.Entry<String, String> entry : environment.entrySet()) {
                    command.add("-e");
                    command.add(entry.getKey() + "='" + entry.getValue() + "'");
                }
            }
            command.add("--name");
            command.add(microservice);
            command.add("--network");
            command.add(NETWORK_NAME);
            command.add(microservice);
            command.add("-c");
            entrypoint = (entrypoint.startsWith("\"") ? "" : "\"") + entrypoint;
            entrypoint = entrypoint + (entrypoint.endsWith("\"") ? "" : "\"");
            command.add(entrypoint);
            ProcessBuilder pb = new ProcessBuilder(command);

            if (inheritIO) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            } else {
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(
                        new File(out.toFile().getAbsolutePath())));
                pb.redirectError(ProcessBuilder.Redirect.appendTo(
                        new File(err.toFile().getAbsolutePath())));
            }
            Process p = pb.start();
            startedProcesses.add(p);
            return p;
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
     * Checks that the Docker image for each passed microservice exists locally.
     *
     * @param microservice microservice name(s)
     */
    private void assertDockerImageBuilt(String... microservice) {
        try {
            for (String m : microservice) {
                Process proc = processHelper.startProcessIgnoreOutput(
                        "docker", "inspect", "--type=image", m
                );
                if (proc.waitFor() != 0) {
                    error(String.format("Image for %s not found. Check that " +
                            "you have built this image and try again.", m));
                    return;
                }
            }
        } catch (InterruptedException e) {
            error(e);
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
                    imgName
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

        List<String> microservicesToRun = new LinkedList<>();
        for (String t : argParser.args()) {
            for (String m : MICROSERVICES) {
                if (m.equals(t)) {
                    microservicesToRun.add(m);
                }
            }
        }

        if (microservicesToRun.isEmpty()) {
            microservicesToRun = List.of(MICROSERVICES);
        }

        // containerized
        if (!local) {

            for (String t : microservicesToRun) {
                buildDockerImage(t);
            }

            String comm = "java -jar -Dspring.profiles.active=container,dev " +
                    "app.jar";

            for (String m : microservicesToRun) {
                runDockerService(m, comm);
            }

            logger.info("Started microservices");
        } else {
            try {
                for (String m : microservicesToRun) {
                    File rundir = new File(
                            System.getProperty("user.dir")
                                    + File.separator + m);
                    ProcessBuilder pb = new ProcessBuilder(
                            runScriptShCmd(rundir.getAbsolutePath() + File.separator + "mvnw"),
                            "spotless:apply",
                            "clean",
                            "compile",
                            "exec:java")
                            .directory(rundir)
                            .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(
                                    String.format(
                                            "%s%s%s.out.log",
                                            getOutDir(),
                                            File.separator,
                                            m
                                    ))
                            ))
                            .redirectError(ProcessBuilder.Redirect.appendTo(new File(
                                    String.format(
                                            "%s%s%s.err.log",
                                            getErrDir(),
                                            File.separator,
                                            m
                                    ))
                            ))
                            .directory(rundir);
                    pb.environment().put("MAVEN_OPTS", "-Dspring.profiles" +
                            ".active=local,dev");
                    startedProcesses.add(pb.start());
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
        assertDockerDaemonRunning();
        int exitCode = 0;

        stop(true);
        stop(false);
        createDockerNetwork();

        boolean local = argParser.hasArg("-l", "--local");
        boolean failstop = argParser.hasArg("--failstop");

        // fetch microservices to test

        List<String> microservicesToTest = new LinkedList<>();
        for (String t : argParser.args()) {
            for (String m : MICROSERVICES) {
                if (m.equals(t)) {
                    microservicesToTest.add(m);
                }
            }
        }

        if (microservicesToTest.isEmpty()) {
            microservicesToTest = List.of(MICROSERVICES);
        }

        // build microservices if not local

        if (!local) {
            try {
                for (String m : MICROSERVICES) {
                    new ProcessBuilder(
                            "docker", "compose", "rm", "-s", "-f", m
                    ).redirectError(ProcessBuilder.Redirect.DISCARD)
                            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                            .start()
                            .waitFor();

                    buildDockerImage(m);
                }
            } catch (IOException | InterruptedException e) {
                error(e);
                return;
            }
        }

        composeDockerHelperServices();

        Map<String, Process> localMicroservices = new HashMap<>();

        try {
            // start all services for dependency reasons
            for (String m : MICROSERVICES) {
                File rundir = new File(
                        System.getProperty("user.dir")
                                + File.separator + m);

                if (!local) {
                    runDockerService(m,
                            "java -jar -Dspring.profiles.active=container," +
                                    "test " +
                                    "app.jar");
                } else {

                    ProcessBuilder pb = new ProcessBuilder(
                            runScriptShCmd(rundir.getAbsolutePath() + File.separator + "mvnw"),
                            "spotless:apply",
                            "clean",
                            "compile",
                            "exec:java")
                            .directory(rundir)
                            .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(
                                    String.format(
                                            "%s%s%s.out.log",
                                            getOutDir(),
                                            File.separator,
                                            m
                                    ))
                            ))
                            .redirectError(ProcessBuilder.Redirect.appendTo(new File(
                                    String.format(
                                            "%s%s%s.err.log",
                                            getErrDir(),
                                            File.separator,
                                            m
                                    ))
                            ))
                            .directory(rundir);
                    pb.environment().put("MAVEN_OPTS", "-Dspring.profiles" +
                            ".active=local,test");
                    Process p = pb.start();
                    startedProcesses.add(p);
                    localMicroservices.put(m, p);
                }

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (Exception ignored) {
                    ;
                }
            }

            logger.info("Started all microservices");

            // stop service, start tests, then restart service

            for (String m : microservicesToTest) {
                if (!local) {

                    // stop service

                    new ProcessBuilder(
                            "docker", "stop", m
                    ).redirectError(ProcessBuilder.Redirect.DISCARD)
                            .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                            .start()
                            .waitFor();

                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception ignored) {
                        ;
                    }


                    logger.info(String.format(
                            "Running tests on %s. Check output and error for " +
                                    "more information", m));


                    // run test

                    String entrypoint = "mvn clean compile test " +
                            "-Dspring.profiles.active=container,test " +
                            "-DargLine=-Dspring.profiles.active=container,test";
                    Process p = runDockerService(m, entrypoint, new HashMap<>(),
                            failstop);
                    assert p != null;
                    int c = p.waitFor();
                    if (c == 0) {
                        logger.info(
                                String.format("All tests passed for %s", m));
                    } else {
                        logger.error(
                                String.format(
                                        "Tests failed for %s. Check output" +
                                                " and error files for more " +
                                                "information.",
                                        m
                                )
                        );
                        exitCode = 1;
                        if (failstop) {
                            break;
                        }
                    }


                    // restart service in normal mode
                    runDockerService(m,
                            "java -jar -Dspring.profiles.active=container," +
                                    "test " +
                                    "app.jar");
                    continue;
                }

                // stop service
                if (localMicroservices.get(m) == null) {
                    logger.warn("Could not detect process for microservice " + m);
                    continue;
                }

                localMicroservices.get(m).destroy();
                localMicroservices.get(m).waitFor();

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (Exception ignored) {
                    ;
                }

                logger.info(String.format(
                        "Running tests on %s. Check output and error for " +
                                "more information", m));

                // run test
                File rundir = new File(
                        System.getProperty("user.dir")
                                + File.separator + m);

                ProcessBuilder pb = new ProcessBuilder(
                        runScriptShCmd(rundir.getAbsolutePath() + File.separator + "mvnw"),
                        "spotless:apply",
                        "clean",
                        "compile",
                        "test",
                        "-DargLine=\"-Dspring.profiles.active=local," +
                                "test\"")
                        .directory(rundir);
                if (failstop) {
                    pb.redirectError(ProcessBuilder.Redirect.INHERIT)
                            .redirectOutput(ProcessBuilder.Redirect.INHERIT);
                } else {
                    pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(
                                    String.format(
                                            "%s%s%s.out.log",
                                            getOutDir(),
                                            File.separator,
                                            m
                                    ))
                            ))
                            .redirectError(ProcessBuilder.Redirect.appendTo(new File(
                                    String.format(
                                            "%s%s%s.err.log",
                                            getErrDir(),
                                            File.separator,
                                            m
                                    ))
                            ));
                }
                pb.environment().put("MAVEN_OPTS", "-Dspring.profiles" +
                        ".active=local,test");
                Process p = pb.start();
                startedProcesses.add(p);
                localMicroservices.put(m, p);

                // wait and restart in normal
                int c = p.waitFor();
                if (c == 0) {
                    logger.info(
                            String.format("All tests passed for %s", m));
                } else {
                    logger.error(
                            String.format(
                                    "Tests failed for %s. Check output" +
                                            " and error files for more " +
                                            "information.",
                                    m
                            )
                    );
                    exitCode = 1;
                }

                // restart service

                pb = new ProcessBuilder(
                        runScriptShCmd(rundir.getAbsolutePath() + File.separator + "mvnw"),
                        "spotless:apply",
                        "clean",
                        "compile",
                        "exec:java")
                        .directory(rundir)
                        .redirectOutput(ProcessBuilder.Redirect.appendTo(new File(
                                String.format(
                                        "%s%s%s.out.log",
                                        getOutDir(),
                                        File.separator,
                                        m
                                ))
                        ))
                        .redirectError(ProcessBuilder.Redirect.appendTo(new File(
                                String.format(
                                        "%s%s%s.err.log",
                                        getErrDir(),
                                        File.separator,
                                        m
                                ))
                        ))
                        .directory(rundir);
                pb.environment().put("MAVEN_OPTS", "-Dspring.profiles" +
                        ".active=local,test");
                p = pb.start();
                startedProcesses.add(p);
                localMicroservices.put(m, p);
            }
        } catch (Exception e) {
            error(e);
            return;
        }

        if (exitCode != 0) {
            cleanup();
            System.exit(1);
        }
    }

    /**
     * Pushes the selected images to harbor, or all if --all passed.
     */
    public void dist() {
        boolean all = false;

        List<String> microservicesToRun = new LinkedList<>();
        for (String t : argParser.args()) {
            for (String m : MICROSERVICES) {
                if (m.equals(t)) {
                    microservicesToRun.add(m);
                }
            }
        }

        if (microservicesToRun.isEmpty()) {
            microservicesToRun = List.of(MICROSERVICES);
        }

        if (microservicesToRun.containsAll(List.of(MICROSERVICES))) {
            all = true;
        }

        if (all && !argParser.hasArg("-y")) {
            Scanner scanner = new Scanner(System.in);
            logger.warn("You're about to push all microservice images to " +
                    "harbor. Continue? (y/N)");
            String resp = scanner.nextLine();
            if (!resp.trim().equalsIgnoreCase("y")) {
                return;
            }
        }

        assertLoggedInToDockerRegistry();

        for (String m : microservicesToRun) {

            assertDockerImageBuilt();

            try {

                String remoteName = HARBOR_URL + m;
                if (processHelper.startProcessIgnoreOutput(
                        "docker", "tag",
                        m, remoteName
                ).waitFor() != 0) {
                    error("Failed to tag image " + m
                            + " as " + remoteName);
                    return;
                }

                Process p = new ProcessBuilder(
                        "docker", "push", remoteName
                ).redirectError(ProcessBuilder.Redirect.INHERIT)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .start();
                if (p.waitFor() != 0) {
                    logger.error(
                            String.format(
                                    "Failed to push image to harbor: %s",
                                    m
                            ));
                }
            } catch (IOException | InterruptedException e) {
                error(e);
                return;
            }
        }
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
     * Stops auxiliary services or only microservices if "--microservices"
     * passed.
     */
    public void stop() {
        if (!argParser.hasArg("--microservices")) {
            stop(true);
            stop(false);
            return;
        }
        stop(true);
    }

    /**
     * Cleans all residue from running this script.
     */
    public void clean() {
        // TODO: implement
    }

    /**
     * Prints the help menu.
     */
    public void help() {
        String banner = """
                                
                .______        ___      .__   __.  __  ___  ___      .______    __    __   __   __       _______ \s
                |   _  \\      /   \\     |  \\ |  | |  |/  / |__ \\     |   _  \\  |  |  |  | |  | |  |     |       \\\s
                |  |_)  |    /  ^  \\    |   \\|  | |  '  /     ) |    |  |_)  | |  |  |  | |  | |  |     |  .--.  |
                |   _  <    /  /_\\  \\   |  . `  | |    <     / /     |   _  <  |  |  |  | |  | |  |     |  |  |  |
                |  |_)  |  /  _____  \\  |  |\\   | |  .  \\   / /_     |  |_)  | |  `--'  | |  | |  `----.|  '--'  |
                |______/  /__/     \\__\\ |__| \\__| |__|\\__\\ |____|    |______/   \\______/  |__| |_______||_______/\s\n""";
        System.out.print(
                Logger.ANSI_GREEN
                        .concat(banner)
                        .concat(Logger.ANSI_RESET)
                        .concat("\n\nScript for developing and testing " +
                                "Bank-2" +
                                " application in local or containerized mode." +
                                " See instructions for more details.\n\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\nhelp ")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("show this menu\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\ndev [--local] [<microservice>*]")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("starts the " +
                                "development stack, which includes the " +
                                "specified microservices plus frontend " +
                                "(always). If no microservices specified, " +
                                "runs all microservices." +
                                " Microservices are started in Docker, unless" +
                                " --local is passed.\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\ntest [--local] [<microservice>*] " +
                                "[--failstop]")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("runs tests" +
                                " on all specified microservices. If no " +
                                "microservices specified, runs tests on all " +
                                "microservices. Microservices are started in " +
                                "Docker, unless --local is passed. If " +
                                "--failstop passed, logging is done to " +
                                "console instead in the logs folder, and the " +
                                "process fails on first test failure (no more" +
                                " tests executed after first failure)\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\ndist [<microservice>*] [-y]")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("pushes the images of" +
                                " the specified microservices to harbor. If " +
                                "no microservices specified, pushes the " +
                                "images of all microservices. (This requires " +
                                "confirmation; use -y to skip.)\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\nstack")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("restart helper services (MariaDB, " +
                                "MongoDB, etc.)\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\nreset")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("reset helper services. Removes all " +
                                "helper services and starts them again. Use " +
                                "this if you're having trouble running the " +
                                "app.\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\nstop [--microservices]")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("stops all running " +
                                "containers, which includes both helper " +
                                "services and microservices. If " +
                                "--microservices specified, then only stops " +
                                "microservices. *NOTE this does NOT stop " +
                                "locally started microservices.\n")
                        .concat(Logger.ANSI_CYAN)
                        .concat("\nclean")
                        .concat(Logger.ANSI_RESET + "\n")
                        .concat("clean residue from running this script. " +
                                "TODO implement\n")
                        .concat("\n")
        );
    }

    /**
     * DANGER!!! For testing the development environment. Executes Docker
     * containers in privileged mode. Do NOT use for app development!
     */
    public void devenv() {
        try {
            for (String t : Arrays.asList(
                    "ubuntu.x64",
                    "ubuntu.aarch64"
            )) {
                String imgName = "test-devenv-" + t.replaceAll("\\.", "-");
                String path = "./docker/test-devenv." + t + ".Dockerfile";
                Process p = new ProcessBuilder(
                        "docker", "build",
                        "-t", imgName,
                        "-f", path,
                        "."
                )
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .start();
                startedProcesses.add(p);
                if (p.waitFor() != 0) {
                    logger.error("Error building " + imgName);
                    continue;
                }

                p = new ProcessBuilder(
                        "docker", "run", "--rm",
                        "--cap-add=NET_ADMIN", "--privileged",
                        "--entrypoint", "/home/project/docker/test-devenv.sh",
                        imgName
                )
                        .redirectError(ProcessBuilder.Redirect.INHERIT)
                        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                        .start();
                startedProcesses.add(p);
                if (p.waitFor() != 0) {
                    logger.error("Dev env testing failed on " + imgName);
                    continue;
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e);
        }
    }

    /**
     * Cleans up all processes before shutting down.
     */
    public void cleanup() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
        for (Process p : startedProcesses) {
            p.destroy();
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