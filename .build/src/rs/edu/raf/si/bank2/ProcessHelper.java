package rs.edu.raf.si.bank2;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Helper methods for easier handling of processes.
 */
public class ProcessHelper {

    /**
     * Executor service for creating out/err handlers.
     */
    private final ExecutorService executorService;

    /**
     * Logger
     */
    private final Logger logger;

    /**
     * Default constructor.
     *
     * @param executorService executor service
     * @param logger          logger
     */
    public ProcessHelper(ExecutorService executorService, Logger logger) {
        this.executorService = executorService;
        this.logger = logger;
    }

    /**
     * Starts a new process with the given strings as command. Outpud and
     * error are redirected to console.
     *
     * @param cmd command and its arguments
     * @return process
     */
    public Process startProcessRedirect(String... cmd) throws IOException {
        return new ProcessBuilder(cmd)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
    }

    /**
     * Starts a new process with the given strings as command. Outpud and
     * error are discarded.
     *
     * @param cmd command and its arguments
     * @return process
     */
    public Process startProcessIgnoreOutput(String... cmd) {
        return startProcessWithOutputError(new ProcessBuilder()
                .command(cmd), (l) -> {
        }, (l) -> {
        });
    }

    /**
     * Starts a new process. Output and error are redirected to console.
     *
     * @param processBuilder process builder
     * @return process
     */
    public Process startProcessRedirect(ProcessBuilder processBuilder) throws IOException {
        return processBuilder
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
    }

    /**
     * Starts a new process. Output and error are discarded.
     *
     * @param processBuilder process builder
     * @return process
     */
    public Process startProcessIgnoreOutput(ProcessBuilder processBuilder) {
        return startProcessWithOutputError(processBuilder, (l) -> {
        }, (l) -> {
        });
    }

    /**
     * Starts a new process. Output is consumed. Errors are discarded.
     *
     * @param processBuilder process builder
     * @param outputConsumer output consumer
     * @return process
     */
    public Process startProcessWithOutput(
            ProcessBuilder processBuilder,
            Consumer<String> outputConsumer
    ) {
        return startProcessWithOutputError(processBuilder, outputConsumer,
                (l) -> {
                });
    }

    /**
     * Starts a new process. Output and errors are consumed.
     *
     * @param processBuilder process builder
     * @param outputConsumer output consumer
     * @param errorConsumer  error consumer
     * @return process
     */
    public Process startProcessWithOutputError(
            ProcessBuilder processBuilder,
            Consumer<String> outputConsumer,
            Consumer<String> errorConsumer
    ) {
        try {
            Process proc = processBuilder.start();
            executorService.submit(
                    StreamGobbler.fromProcess(
                            proc,
                            StreamGobbler.Pipe.OUT,
                            outputConsumer)
            );
            executorService.submit(
                    StreamGobbler.fromProcess(
                            proc,
                            StreamGobbler.Pipe.ERR,
                            errorConsumer)
            );
            return proc;
        } catch (IOException e) {
            this.logger.error(e);
            return null;
        }
    }
}
