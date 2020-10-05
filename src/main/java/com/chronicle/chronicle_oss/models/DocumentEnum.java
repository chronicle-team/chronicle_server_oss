package com.chronicle.chronicle_oss.models;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

public enum DocumentEnum {
    DOCX("application/msword"),
    PDF("application/pdf");

    @Getter
    private String type;

    DocumentEnum(String type) {
        this.type = type;
    }

    public static Optional<DocumentEnum> valueOfType(String templateType) {
        return Arrays.stream(DocumentEnum.values())
                .filter(type-> type.getType().equals(templateType))
                .findFirst();
    }
}
