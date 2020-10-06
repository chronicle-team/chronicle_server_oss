package com.chronicle.chronicle_oss.repositories;

import com.chronicle.chronicle_oss.models.Config;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends CrudRepository<Config, String> {
}
