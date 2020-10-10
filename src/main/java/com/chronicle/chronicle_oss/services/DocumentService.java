package com.chronicle.chronicle_oss.services;

import com.chronicle.chronicle_oss.exceptions.BadRequestException;
import com.chronicle.chronicle_oss.exceptions.InternalServerError;
import com.chronicle.chronicle_oss.models.Field;
import com.chronicle.chronicle_oss.models.FieldConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.spire.doc.Document;
import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.dynabean.DynaClassCache;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.beanutils.DynaBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class DocumentService {
    private ObjectMapper objectMapper;
    private ConfigService configService;
    private Platform platform;
    private Database database;

    @Value("${chronicle.storage}")
    private String documentStorage;

    public DocumentService(ObjectMapper objectMapper, ConfigService configService, Platform platform, Database database) {
        this.objectMapper = objectMapper;
        this.configService = configService;
        this.platform = platform;
        this.database = database;
    }

    public Map<String, Field> parseDocumentData(String data, Map<String, FieldConfig> jsonConfig, boolean keepEmpty) {
        Map<String, Object> documentData;
        try {
            documentData = objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error(e);
            return Collections.emptyMap();
        }
        return parseDocumentData(documentData, jsonConfig, keepEmpty);
    }

    public Map<String, Field> parseDocumentData(Map<String, Object> data, Map<String, FieldConfig> jsonConfig, boolean keepEmpty) {
        Set<String> unknownFields = Sets.difference(data.keySet(), jsonConfig.keySet());
        if (!unknownFields.isEmpty()) {
            throw new BadRequestException(unknownFields);
        }

        return jsonConfig.entrySet().stream()
                .filter(configEntry-> data.containsKey(configEntry.getKey()) || (keepEmpty && !data.containsKey(configEntry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, e -> createField(e.getKey(), e.getValue(), data.getOrDefault(e.getKey(), null))));
    }

    public Field createField(String key, FieldConfig fieldConfig, Object value) {
        return new Field(key, fieldConfig.getFieldType(), value);
    }

    public void saveDocument(String documentName, Map<String, Field> documentData) {
        Table table = database.findTable(documentName);
        DynaBean newInstance = new DynaClassCache().createNewInstance(table);
        documentData.forEach((key, field) -> newInstance.set(key, field.getFieldValue()));
        platform.insert(database, newInstance);
    }

    public Map<String, Field> getDocument(String documentName, Set<Map<String, Field>> parsedConditionals) {
        Map<String, FieldConfig> configJson = configService.getConfigJson(documentName);
        Set<String> documentFields = configJson.keySet();
        String conditionalQuery = parsedConditionals.stream()
                .map(fields -> fields.values().stream()
                        .map(field -> field.getFieldKey() + " = " + field.getFieldValue())
                        .collect(Collectors.joining(" AND ")))
                .collect(Collectors.joining(" OR "));
        List<DynaBean> results = platform.fetch(database, "SELECT " + String.join(", ", documentFields) + " FROM " + documentName + " WHERE " + conditionalQuery);
        if (results.isEmpty()) {
            return Collections.emptyMap();
        }
        DynaBean firstResult = results.get(0);
        Map<String, Object> rawDocumentData = documentFields.stream().collect(Collectors.toMap(Function.identity(), firstResult::get));
        return parseDocumentData(rawDocumentData, configJson, true);
    }

    public File getDocDocument(String documentName, byte[] template, Map<String, Field> documentData) {
        try (InputStream is = new ByteArrayInputStream(template)) {
            Document doc = new Document(is);
            documentData.forEach((key, value) ->
                    doc.replace("{" + documentName + "." + key + "}", value.getFieldValueAsString(), false, true));

            Path path = Paths.get(documentStorage, documentName + ".doc");
            File file = path.toFile();
            Files.createParentDirs(file);
            Files.touch(file);
            doc.saveToFile(path.toString());
            return file;
        } catch (Exception e) {
            log.error(e);
            throw new InternalServerError();
        }
    }
}
