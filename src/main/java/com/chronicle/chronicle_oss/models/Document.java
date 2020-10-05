package com.chronicle.chronicle_oss.models;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String json;
    private DocumentEnum templateType;
    @Column(length = 5000)
    private byte[] template;
}
