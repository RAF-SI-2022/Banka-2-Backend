package rs.edu.raf.si.bank2.otc.models.mongodb.pdf;

import java.io.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {
    private final File file;

    private String subType = "vnd.openxmlformats-officedocument.wordprocessingml.document";

    public CustomMultipartFile(File file) {
        this.file = file;
    }

    public CustomMultipartFile(File file, String type) {
        this.file = file;
        this.subType = type;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getOriginalFilename() {
        return file.getName();
    }

    @Override
    public String getContentType() {
        // Replace this with the appropriate content type for your file
        return "application/" + subType;
    }

    @Override
    public boolean isEmpty() {
        return file.length() == 0;
    }

    @Override
    public long getSize() {
        return file.length();
    }

    @Override
    public byte[] getBytes() throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            return StreamUtils.copyToByteArray(inputStream);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (InputStream inputStream = new FileInputStream(file);
                OutputStream outputStream = new FileOutputStream(dest)) {
            StreamUtils.copy(inputStream, outputStream);
        }
    }
}
