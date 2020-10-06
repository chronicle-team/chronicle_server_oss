package com.chronicle.chronicle_oss.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "configs")
public class Config {
    @Id
    private String name;
    private String json;
    private DocumentType templateType;
    @Column(length = 5000)
    private byte[] template;
}
