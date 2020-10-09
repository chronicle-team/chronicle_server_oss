package com.chronicle.chronicle_oss.services;

import com.chronicle.chronicle_oss.models.FieldConfig;
import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.model.Column;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.model.Table;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DatabaseService {

    private Platform platform;
    private Database database;

    public DatabaseService(Platform platform, Database database) {
        this.platform = platform;
        this.database = database;
    }

    private Column createColumn(String name, FieldConfig fieldConfig) {
        Column column = new Column();
        column.setName(name);
        column.setType(fieldConfig.getFieldType().getDatabaseType());
        column.setAutoIncrement(fieldConfig.isAutoIncrement());
        column.setPrimaryKey(fieldConfig.isPrimaryKey());
        return column;
    }

    public void createAndSaveTable(String tableName, Map<String, FieldConfig> config) {
        Table table = createTable(tableName, config);
        database.addTable(table);
        platform.createModel(database, false, false);
    }

    private Table createTable(String tableName, Map<String, FieldConfig> config) {
        List<Column> columns = config.entrySet().stream()
                .map(field -> createColumn(field.getKey(), field.getValue()))
                .collect(Collectors.toList());

        Table table = new Table();
        table.setName(tableName);
        table.addColumns(columns);
        return table;
    }
}
