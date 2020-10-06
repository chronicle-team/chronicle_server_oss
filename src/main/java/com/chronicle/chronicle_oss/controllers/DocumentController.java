package com.chronicle.chronicle_oss.controllers;

import com.chronicle.chronicle_oss.exceptions.BadRequestException;
import com.chronicle.chronicle_oss.models.Config;
import com.chronicle.chronicle_oss.models.FieldConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("documents")
public class DocumentController {

    private ConfigController configController;
    private ObjectMapper objectMapper;

    @Autowired
    public DocumentController(ConfigController configController, ObjectMapper objectMapper) {
        this.configController = configController;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public void addDocument(String configName, String data) throws JsonProcessingException {
        Config config = configController.getConfig(configName).orElseThrow(BadRequestException::new);
        Map<String, FieldConfig> jsonConfig = objectMapper.readValue(config.getJson(), new TypeReference<Map<String, FieldConfig>>() {
        });
        Map<String, Object> documentData = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
        });

        documentData.forEach((key, value) -> {
            FieldConfig fieldConfig = jsonConfig.get(key);
            if (fieldConfig == null) {
                throw new BadRequestException(key);
            }

            Class javaClass = fieldConfig.getFieldType().getJavaClass();
            Object cast = javaClass.cast(value);

            // save in db
        });
    }
}
