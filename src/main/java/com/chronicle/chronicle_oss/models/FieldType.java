package com.chronicle.chronicle_oss.models;

import lombok.Getter;

import static de.elnarion.ddlutils.model.TypeMap.VARCHAR;

public enum FieldType {
    STRING(VARCHAR, String.class);

    @Getter
    private final String databaseType;
    @Getter
    private final Class javaClass;

    FieldType(String databaseType, Class javaClass) {
        this.databaseType = databaseType;
        this.javaClass = javaClass;
    }
}
