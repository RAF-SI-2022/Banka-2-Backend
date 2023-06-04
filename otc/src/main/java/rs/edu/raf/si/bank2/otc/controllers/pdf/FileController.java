package rs.edu.raf.si.bank2.otc.controllers.pdf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rs.edu.raf.si.bank2.otc.models.mongodb.Contract;
import rs.edu.raf.si.bank2.otc.models.mongodb.ContractElements;
import rs.edu.raf.si.bank2.otc.models.mongodb.TransactionElement;
import rs.edu.raf.si.bank2.otc.models.mongodb.pdf.LoadFile;
import rs.edu.raf.si.bank2.otc.services.pdf.FileService;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file) throws IOException {
        ContractElements contractElements1 = ContractElements.FINALISED;
        TransactionElement transactionElement = new TransactionElement("1234",ContractElements.SELL,"STOCK",ContractElements.FINALISED,"USD",45.5,12.2);
        TransactionElement transactionElement2 = new TransactionElement("1234",ContractElements.SELL,"STOCK",ContractElements.FINALISED,"USD",45.5,12.2);
        TransactionElement transactionElement3 = new TransactionElement("1234",ContractElements.SELL,"STOCK",ContractElements.FINALISED,"USD",45.5,12.2);
        Contract contract = new Contract("123", contractElements1,"creation","last","number","desc", List.of(transactionElement, transactionElement2, transactionElement3));
        File f = fileService.createContractFile(contract);
        fileService.saveFile(f);
        return new ResponseEntity<>(fileService.addFile(file), HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable String id) throws IOException {
        LoadFile loadFile = fileService.downloadFile(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(loadFile.getFileType() ))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + loadFile.getFilename() + "\"")
                .body(new ByteArrayResource(loadFile.getFile()));
    }

}