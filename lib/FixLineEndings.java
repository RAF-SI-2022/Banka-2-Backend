import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Fixes line endings (CR/CRLF/LF > LF only) in critical files.
 */
public class FixLineEndings {

    private static final String SEP = File.separator;

    public static void main(String[] args) {
        String cwdir = System.getProperty("user.dir");
        String[] files = new String[]{
                "Makefile",
                "mvnw",
                "docker" + SEP + "test-devenv.sh",
                "docker" + SEP + "healthcheck" + SEP + "mariadb" + SEP + "healthcheck.sh",
                "docker" + SEP + "healthcheck" + SEP + "mongodb" + SEP + "healthcheck.sh"
        };
        for (String fname : files) {
            try {
                Path p = Paths.get(cwdir + SEP + fname);
                String content = new String(Files.readAllBytes(p));
                content = content.replaceAll("\\r\\n?", "\n");
                Files.write(p, content.getBytes());
                System.out.println("Wrote file: " + fname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}