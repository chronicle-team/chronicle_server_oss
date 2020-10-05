package com.chronicle.chronicle_oss.repositories;

import com.chronicle.chronicle_oss.models.Document;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {
}
