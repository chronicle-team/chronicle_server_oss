package com.chronicle.chronicle_oss.models;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum DocumentType {
    DOCX("application/msword"),
    PDF("application/pdf");

    @Getter
    private String type;

    DocumentType(String type) {
        this.type = type;
    }

    public static Optional<DocumentType> valueOfType(String templateType) {
        return Arrays.stream(DocumentType.values())
                .filter(type-> type.getType().equals(templateType))
                .findFirst();
    }
}
