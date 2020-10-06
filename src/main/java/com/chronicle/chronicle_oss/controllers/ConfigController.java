package com.chronicle.chronicle_oss.controllers;

import com.chronicle.chronicle_oss.models.Config;
import com.chronicle.chronicle_oss.models.DocumentType;
import com.chronicle.chronicle_oss.models.FieldConfig;
import com.chronicle.chronicle_oss.repositories.ConfigRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("config")
public class ConfigController {

    private Platform platform;
    private Database database;
    private ObjectMapper objectMapper;
    //TODO: document service with validation
    private ConfigRepository configRepository;

    @Autowired
    public ConfigController(Platform platform, Database database, ObjectMapper objectMapper, ConfigRepository configRepository) {
        this.platform = platform;
        this.database = database;
        this.objectMapper = objectMapper;
        this.configRepository = configRepository;
    }

    @GetMapping
    public List<Config> getAllConfigs() {
        return StreamSupport.stream(configRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addConfig(@RequestParam String configName,
                          @RequestParam String jsonConfig,
                          @RequestParam DocumentType templateType,
                          @RequestParam MultipartFile template) throws IOException {
        Config config = Config.builder().name(configName)
                .json(jsonConfig)
                .templateType(templateType)
                .template(template.getBytes())
                .build();

        Map<String, FieldConfig> configMap = objectMapper.readValue(jsonConfig, new TypeReference<Map<String, FieldConfig>>() {
        });

        List<Column> columns = configMap.entrySet().stream()
                .map(field -> createColumn(field.getKey(), field.getValue()))
                .collect(Collectors.toList());

        Table table = new Table();
        table.setName(configName);
        table.addColumns(columns);

        database.addTable(table);
        platform.createModel(database, false, false);
        configRepository.save(config);
    }

    private Column createColumn(String name, FieldConfig fieldConfig) {
        Column column = new Column();
        column.setName(name);
        column.setType(fieldConfig.getFieldType().getDatabaseType());
        column.setAutoIncrement(fieldConfig.isAutoIncrement());
        column.setPrimaryKey(fieldConfig.isPrimaryKey());
        return column;
    }

    public Optional<Config> getConfig(String configName) {
        return configRepository.findById(configName);
    }
}
