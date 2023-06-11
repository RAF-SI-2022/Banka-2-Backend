package rs.edu.raf.si.bank2.client.services.pdf;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.edu.raf.si.bank2.client.models.mongodb.Contract;
import rs.edu.raf.si.bank2.client.models.mongodb.pdf.CustomMultipartFile;
import rs.edu.raf.si.bank2.client.models.mongodb.pdf.LoadFile;

import java.io.*;
import java.nio.file.Path;
@Service
public class FileService {

    @Autowired
    private GridFsTemplate template;

    @Autowired
    private GridFsOperations operations;

    public String createPDFFile(Contract contract) throws IOException {
        File file = new File("Contract2");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(file,contract);
        System.out.println("The Object  was successfully written to a file");
        File file1 = new File("Proba.pdf");
        try {
            // Create a new PDF document
            PDDocument document = new PDDocument();

            // Create a blank page and add it to the document
            PDPage page = new PDPage();
            document.addPage(page);
            // Create a new content stream for the page
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Set the font and font size
            contentStream.setFont(PDType1Font.COURIER, 12);

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line = reader.readLine();
                float y = page.getMediaBox().getHeight() - 50;
                if (line != null) {
                    String[] splitValues = line.split(",");  // Split the line by the character

                    for (String value : splitValues) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                        contentStream.showText(value);
                        contentStream.endText();
                        y -= 12;
                    }
                }
            } catch (IOException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }


            contentStream.close();

            // Save the document to a file
            document.save(file1);

            // Close the document
            document.close();

            System.out.println("PDF created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultipartFile multipartFile = new CustomMultipartFile(file1, "pdf");
        return addFile(multipartFile);
    }
    public String addFile(MultipartFile upload) throws IOException {

        DBObject metadata = new BasicDBObject();
        metadata.put("fileSize", upload.getSize());
        Object fileID = template.store(upload.getInputStream(), upload.getOriginalFilename(), upload.getContentType(), metadata);
        return fileID.toString();
    }


    public LoadFile downloadFile(String id) throws IOException {

        GridFSFile gridFSFile = template.findOne( new Query(Criteria.where("_id").is(id)) );

        LoadFile loadFile = new LoadFile();

        if (gridFSFile != null && gridFSFile.getMetadata() != null) {
            loadFile.setFilename( gridFSFile.getFilename() );

            loadFile.setFileType( gridFSFile.getMetadata().get("_contentType").toString() );

            loadFile.setFileSize( gridFSFile.getMetadata().get("fileSize").toString() );

            loadFile.setFile( IOUtils.toByteArray(operations.getResource(gridFSFile).getInputStream()) );
        }
        return loadFile;
    }
    public void downloadFileFromMongoDB(String id){
        GridFSFile gridFSFile = template.findOne( new Query(Criteria.where("_id").is(id)) );

        String databaseName = "dev";
        String bucketName = "files";
        ObjectId fileId = new ObjectId(id); // Replace with the actual file ObjectId

        String outputFilePath = "downloadedFile"; // Replace with the desired local file path

        try {
            MongoClient mongoClient = MongoClients.create("mongodb://root:root@localhost:27017/?authMechanism=DEFAULT");
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            GridFSBucket gridFSBucket = GridFSBuckets.create(database, bucketName);

            //GridFSFile file = gridFSBucket.find(new BsonObjectId(fileId)).first();
            if (gridFSFile != null) {
                Path outputPath = Path.of(outputFilePath);
                FileOutputStream outputStream = new FileOutputStream(outputPath.toFile());
                gridFSBucket.downloadToStream(fileId, outputStream);
                outputStream.close();
                System.out.println("File converted successfully.");
            } else {
                System.out.println("File not found in GridFS.");
            }

            mongoClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}