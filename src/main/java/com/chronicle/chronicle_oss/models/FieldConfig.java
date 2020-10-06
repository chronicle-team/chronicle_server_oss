package com.chronicle.chronicle_oss.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Data
public class FieldConfig {
    @JsonProperty("fieldType")
    private FieldType fieldType;
    @JsonProperty("isAutoIncrement")
    private boolean isAutoIncrement;
    @JsonProperty("isPrimaryKey")
    private boolean isPrimaryKey;
}
