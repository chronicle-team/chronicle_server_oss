package com.chronicle.chronicle_oss.controllers;

import com.spire.doc.*;
import com.chronicle.chronicle_oss.exceptions.BadRequestException;
import com.chronicle.chronicle_oss.models.Config;
import com.chronicle.chronicle_oss.models.FieldConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.dynabean.DynaClassCache;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import org.apache.commons.beanutils.DynaBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("documents")
public class DocumentController {

    private ConfigController configController;
    private ObjectMapper objectMapper;

    private Platform platform;
    private Database database;

    @Autowired
    public DocumentController(ConfigController configController, ObjectMapper objectMapper, Platform platform, Database database) {
        this.configController = configController;
        this.objectMapper = objectMapper;
        this.platform = platform;
        this.database = database;
    }

    @GetMapping
    public void getFile(String configName) throws JsonProcessingException {
        Config config = configController.getConfig(configName).orElseThrow(BadRequestException::new);
        Map<String, FieldConfig> jsonConfig = objectMapper.readValue(config.getJson(), new TypeReference<Map<String, FieldConfig>>() {
        });
        List<DynaBean> fetch = platform.fetch(database, "SELECT * FROM " + configName);
        try (InputStream is = Resources.getResource("Request.docx").openStream()) {
            Document doc = new Document(is);
            for (Map.Entry<String, FieldConfig> entry : jsonConfig.entrySet()) {
                String k = entry.getKey();
                FieldConfig v = entry.getValue();
                Optional<Object> first = fetch.stream().map(f -> f.get(k)).findFirst();
                if (first.isPresent()) {
                    doc.replace("{" + configName + "." + k + "}", first.get().toString(), false, true);
                }
            }
            doc.saveToFile("new.doc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping
    public void addDocument(String configName, String data) throws JsonProcessingException {
        Config config = configController.getConfig(configName).orElseThrow(BadRequestException::new);
        Map<String, FieldConfig> jsonConfig = objectMapper.readValue(config.getJson(), new TypeReference<Map<String, FieldConfig>>() {
        });
        Map<String, Object> documentData = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
        });

        Table table = database.findTable(configName);
        DynaBean newInstance = new DynaClassCache().createNewInstance(table);

        documentData.forEach((key, value) -> {
            FieldConfig fieldConfig = jsonConfig.get(key);
            if (fieldConfig == null) {
                throw new BadRequestException(key);
            }

            Class javaClass = fieldConfig.getFieldType().getJavaClass();
            Object cast = javaClass.cast(value);
            newInstance.set(key, cast);
            // save in db
        });
        platform.insert(database, newInstance);
    }
}
