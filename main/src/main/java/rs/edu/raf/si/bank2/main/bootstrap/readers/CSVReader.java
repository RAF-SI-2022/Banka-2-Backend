package rs.edu.raf.si.bank2.main.bootstrap.readers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

/**
 * Convenience class for reading CSV files and parsing their content.
 */
public class CSVReader {

    private final Logger logger = LoggerFactory.getLogger(CommandLineRunner.class);

    private CSVReader() {}

    /**
     * Singleton instance of the class.
     *
     * @return singleton instance
     */
    public static CSVReader getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Opens the CSV file and reads it as a stream. Returns a stream of lines in the file, or empty if errored.
     *
     * @param csvPath path to the file or empty if errored
     * @return stream or empty if failed
     */
    public Optional<Stream<String>> readCSVStream(String csvPath) {
        if (csvPath == null) {
            throw new IllegalArgumentException("csvPath must not be null");
        }
        if (!csvPath.startsWith("/")) {
            csvPath = "/" + csvPath;
        }

        InputStream in;
        try {
            in = getClass().getResourceAsStream(csvPath);
        } catch (Exception e) {
            logger.error("Failed to fetch resource " + csvPath, e);
            return Optional.empty();
        }

        if (in == null) {
            logger.error("Fetched resource is null: " + csvPath);
            return Optional.empty();
        }

        // TODO multiple errors with this approach, for one reading the entire
        //  file, and two it may not be UTF_8
        String all;
        try {
            all = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to read bytes from " + csvPath, e);
            return Optional.empty();
        }

        // TODO delimeter may not be good
        return Optional.of(Stream.of(all.split("\\n")));
    }

    /**
     * Reads the CSV resource file as a string.
     *
     * @param csvPath path to the CSV file
     * @return string or empty if failed
     */
    public Optional<String> readCSVString(String csvPath) {
        if (csvPath == null) {
            throw new IllegalArgumentException("csvPath must not be null");
        }
        if (!csvPath.startsWith("/")) {
            csvPath = "/" + csvPath;
        }

        InputStream in;
        try {
            in = getClass().getResourceAsStream(csvPath);
        } catch (Exception e) {
            logger.error("Failed to fetch resource " + csvPath, e);
            return Optional.empty();
        }

        if (in == null) {
            logger.error("Fetched resource is null: " + csvPath);
            return Optional.empty();
        }

        // TODO multiple errors with this approach, for one reading the entire
        //  file, and two it may not be UTF_8
        String all;
        try {
            all = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Failed to read bytes from " + csvPath, e);
            return Optional.empty();
        }

        return Optional.of(all);
    }

    /**
     * Singleton holder.
     */
    private class Holder {
        private static final CSVReader INSTANCE = new CSVReader();
    }
}
