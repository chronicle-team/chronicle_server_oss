package com.chronicle.chronicle_oss.controllers;

import com.chronicle.chronicle_oss.exceptions.NotFoundException;
import com.chronicle.chronicle_oss.models.DocumentType;
import com.chronicle.chronicle_oss.models.Field;
import com.chronicle.chronicle_oss.models.FieldConfig;
import com.chronicle.chronicle_oss.services.ConfigService;
import com.chronicle.chronicle_oss.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("documents")
public class DocumentController {

    private ConfigService configService;
    private DocumentService documentService;

    @Autowired
    public DocumentController(ConfigService configService, DocumentService documentService) {
        this.configService = configService;
        this.documentService = documentService;
    }

    @PutMapping
    /**
     * documentName - documentName
     * conditionals - batch of AND-conditionals
     */
    public @ResponseBody File getFile(String documentName, String[] conditionals) {
        Map<String, FieldConfig> jsonConfig = configService.getConfigJson(documentName);
        Set<Map<String, Field>> parsedConditionals = Arrays.stream(conditionals)
                .map(data -> documentService.parseDocumentData(data, jsonConfig, false))
                .collect(Collectors.toSet());
        Map<String, Field> documentData = documentService.getDocument(documentName, parsedConditionals);
        DocumentType templateType = configService.getTemplateType(documentName);
        byte[] template = configService.getTemplate(documentName);
        switch (templateType) {
            case DOCX:
                return documentService.getDocDocument(documentName, template, documentData);
        }

        throw new NotFoundException(documentName);
    }

    @PostMapping
    public void addDocument(String documentName, String data) {
        Map<String, FieldConfig> jsonConfig = configService.getConfigJson(documentName);
        Map<String, Field> documentData = documentService.parseDocumentData(data, jsonConfig, true);
        documentService.saveDocument(documentName, documentData);
    }
}
