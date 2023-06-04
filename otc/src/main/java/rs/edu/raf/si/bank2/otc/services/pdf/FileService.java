package rs.edu.raf.si.bank2.otc.services.pdf;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.pdf.CustomMultipartFile;
import rs.edu.raf.si.bank2.otc.models.mongodb.pdf.LoadFile;

import java.io.*;

@Service
public class FileService {

    @Autowired
    private GridFsTemplate template;

    @Autowired
    private GridFsOperations operations;

    public File createContractFile(Contract contract) throws IOException {
        File file = new File("Contract.pdf");
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(contract.toString());
        objectOut.close();
        System.out.println("The Object  was succesfully written to a file");
        return file;
    }

    public String saveFile(File file) throws IOException {
        MultipartFile multipartFile = new CustomMultipartFile(file);
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

}