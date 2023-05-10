package rs.edu.raf.si.bank2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Handles IO with processes. See
 * <a href="https://www.baeldung.com/run-shell-command-in-java#Output">...</a>
 */
public class StreamGobbler implements Runnable {

    /**
     * Consumer that discards any input.
     */
    public static final Consumer<String> NUL = (l) -> {
    };
    private final InputStream inputStream;
    private final Consumer<String> consumer;

    /**
     * Default constructor.
     *
     * @param inputStream standard input for the process
     * @param consumer    consumer
     */
    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }

    /**
     * Creates a new StreamGobbler for this process' output. Consumes the
     * output without doing anything.
     *
     * @param process the process to read output for
     * @return StreamGobbler instance
     */
    public static StreamGobbler consumeOut(Process process) {
        return new StreamGobbler(process.getInputStream(), (l) -> {
            ;
        });
    }

    /**
     * Creates a new StreamGobbler for this process' stderr. Consumes stderr
     * without doing anything.
     *
     * @param process the process to read stderr for
     * @return StreamGobbler instance
     */
    public static StreamGobbler consumeErr(Process process) {
        return new StreamGobbler(process.getErrorStream(), (l) -> {
            ;
        });
    }

    /**
     * Creates a new StreamGobbler for this process' selected pipe.
     *
     * @param process  the process to read output for
     * @param pipe     output or error
     * @param consumer consumer
     * @return StreamGobbler instance
     */
    public static StreamGobbler fromProcess(
            Process process,
            Pipe pipe,
            Consumer<String> consumer
    ) {
        return new StreamGobbler(
                pipe == Pipe.ERR ?
                        process.getErrorStream() :
                        process.getInputStream(),
                consumer);
    }

    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
                .forEach(consumer);
    }

    public enum Pipe {
        OUT,
        ERR,
    }
}
