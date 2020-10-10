package com.chronicle.chronicle_oss.models;

import com.chronicle.chronicle_oss.exceptions.BadRequestException;
import lombok.Data;

import java.io.Serializable;

@Data
public class Field implements Serializable {
    private String fieldKey;
    private FieldType fieldType;
    private Object fieldValue;

    public Field(String fieldKey, FieldType fieldType, Object value) {
        this.fieldKey = fieldKey;
        this.fieldType = fieldType;
        setValue(fieldKey, fieldType, value);
    }

    private void setValue(String fieldKey, FieldType fieldType, Object value) {
        try {
            this.fieldValue = fieldType.getJavaClass().cast(value);
        } catch (ClassCastException e) {
            throw new BadRequestException(fieldKey, fieldType.getJavaClass());
        }
    }

    public String getFieldValueAsString() {
        return fieldValue.toString();
    }
}
