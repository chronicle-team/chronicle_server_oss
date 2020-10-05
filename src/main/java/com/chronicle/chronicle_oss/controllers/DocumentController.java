package com.chronicle.chronicle_oss.controllers;

import com.chronicle.chronicle_oss.models.Document;
import com.chronicle.chronicle_oss.models.DocumentEnum;
import com.chronicle.chronicle_oss.repositories.DocumentRepository;
import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import de.elnarion.ddlutils.model.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("config")
public class DocumentController {

    private Platform platform;
    private Database database;
    //TODO: document service with validation
    private DocumentRepository documentRepository;

    @Autowired
    public DocumentController(Platform platform, Database database, DocumentRepository documentRepository) {
        this.platform = platform;
        this.database = database;
        this.documentRepository = documentRepository;
    }

    @GetMapping
    public List<Document> getAllDocuments() {
        return StreamSupport.stream(documentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addDocument(@RequestParam String name, @RequestParam String json, @RequestParam DocumentEnum templateType, @RequestParam MultipartFile template) throws IOException {
//        DocumentEnum documentEnum = DocumentEnum.valueOfType(templateType).orElseThrow(() -> new IllegalArgumentException(templateType));
        Document build = Document.builder().name(name)
                .json(json)
                .templateType(templateType)
                .template(template.getBytes())
                .build();

        Table table = new Table();
        Column column = new Column();
        //TODO create columns form json
        column.setName("aaaa");
        column.setType(TypeMap.VARCHAR);
        table.setName(name);
        table.addColumn(column);
        database.addTable(table);

        platform.createModel(database, false, false);

        documentRepository.save(build);
    }
}
