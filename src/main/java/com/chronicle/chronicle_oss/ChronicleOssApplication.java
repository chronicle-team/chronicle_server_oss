package com.chronicle.chronicle_oss;

import de.elnarion.ddlutils.Platform;
import de.elnarion.ddlutils.PlatformFactory;
import de.elnarion.ddlutils.model.Database;
import de.elnarion.ddlutils.platform.PlatformImplBase;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootApplication
@EnableJpaRepositories
public class ChronicleOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChronicleOssApplication.class, args);
    }

    @Autowired
    @Bean
    public Database createDatabase(DataSource dataSource) throws SQLException {
        return new Database(dataSource.getConnection().getMetaData().getURL());
    }

    @Autowired
    @Bean
    public Platform createPlatform(DataSource dataSource) {
        return PlatformFactory.createNewPlatformInstance(dataSource);
    }

}
