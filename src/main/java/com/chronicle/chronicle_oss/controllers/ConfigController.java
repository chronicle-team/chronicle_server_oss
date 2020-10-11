package com.chronicle.chronicle_oss.controllers;

import com.chronicle.chronicle_oss.exceptions.BadRequestException;
import com.chronicle.chronicle_oss.models.Config;
import com.chronicle.chronicle_oss.models.DocumentType;
import com.chronicle.chronicle_oss.models.FieldConfig;
import com.chronicle.chronicle_oss.services.ConfigService;
import com.chronicle.chronicle_oss.services.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("config")
public class ConfigController {

    private DatabaseService databaseService;
    private ConfigService configService;

    @Autowired
    public ConfigController(DatabaseService databaseService, ConfigService configService) {
        this.configService = configService;
        this.databaseService = databaseService;
    }

    @GetMapping
    public List<Config> getAllConfigs() {
        return configService.getAllConfigs();
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

        Map<String, FieldConfig> configMap = configService.parseConfig(jsonConfig);
        try {
            configService.validateJson(configMap);
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException(e.toString());
        }
        databaseService.createAndSaveTable(configName, configMap);
        configService.saveConfig(config);
    }
}
