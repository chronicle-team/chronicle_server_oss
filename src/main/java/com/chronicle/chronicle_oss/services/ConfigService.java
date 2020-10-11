package com.chronicle.chronicle_oss.services;

import com.chronicle.chronicle_oss.exceptions.BadRequestException;
import com.chronicle.chronicle_oss.exceptions.NotFoundException;
import com.chronicle.chronicle_oss.models.Config;
import com.chronicle.chronicle_oss.models.DocumentType;
import com.chronicle.chronicle_oss.models.FieldConfig;
import com.chronicle.chronicle_oss.models.FieldType;
import com.chronicle.chronicle_oss.repositories.ConfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@Service
public class ConfigService {

    private ObjectMapper objectMapper;
    private ConfigRepository configRepository;

    @Autowired
    public ConfigService(ObjectMapper objectMapper, ConfigRepository configRepository) {
        this.objectMapper = objectMapper;
        this.configRepository = configRepository;
    }

    public Map<String, FieldConfig> parseConfig(String config) {
        try {
            return objectMapper.readValue(config, new TypeReference<Map<String, FieldConfig>>() {
            });
        } catch (JsonProcessingException e) {
            log.error(e);
            return Collections.emptyMap();
        }
    }

    public List<Config> getAllConfigs() {
        return StreamSupport.stream(configRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<Config> getConfig(String configName) {
        return configRepository.findById(configName);
    }

    public Map<String, FieldConfig> getConfigJson(String configName) {
        Config config = getConfig(configName).orElseThrow(BadRequestException::new);
        return parseConfig(config.getJson());
    }

    public void saveConfig(Config config) {
        configRepository.save(config);
    }

    public byte[] getTemplate(String documentName) {
        return getConfig(documentName).map(Config::getTemplate).orElseThrow(() -> new NotFoundException(documentName));
    }

    public DocumentType getTemplateType(String documentName) {
        return getConfig(documentName).map(Config::getTemplateType).orElseThrow(() -> new NotFoundException(documentName));
    }

    public void validateJson(Map<String, FieldConfig> config) {
        config.forEach((k, v) -> {
            if (v.getFieldType() != FieldType.INT && (v.isAutoIncrement())) {
                throw new IllegalArgumentException("Auto Increment should be INT");
            }
        });
    }
}
