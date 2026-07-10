package com.tdsi.spring_multidatasource.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * ===================================================================
 * Configuration DataSource ARCHIVE (Secondaire)
 * ===================================================================
 * Cette classe configure :
 * - Le DataSource pour la base "archive"
 * - L'EntityManagerFactory dédié
 * - Le TransactionManager dédié
 * - Les repositories Spring Data JPA
 *
 * Pas d'annotation @Primary car c'est la datasource secondaire.
 * Il faut toujours utiliser @Qualifier("archiveDataSource") pour l'injecter.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        // Package contenant les repositories de la base archive
        basePackages = "com.tdsi.spring_multidatasource.repository.archive",
        // EntityManagerFactory dédié
        entityManagerFactoryRef = "archiveEntityManagerFactory",
        // TransactionManager dédié
        transactionManagerRef = "archiveTransactionManager"
)
public class ArchiveDataSourceConfig {

    /**
     * ----------------------------------------------------------------
     * Properties du DataSource Archive
     * ----------------------------------------------------------------
     * Charge les proprietes depuis le prefixe "spring.datasource.archive"
     * dans le fichier application.yml
     */
    @Bean(name = "archiveDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.archive")
    public DataSourceProperties archiveDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * ----------------------------------------------------------------
     * DataSource Archive (Pool de connexions)
     * ----------------------------------------------------------------
     * Cree le DataSource HikariCP pour la base archive.
     * Pas de @Primary car c'est la datasource secondaire.
     */
    @Bean(name = "archiveDataSource")
    public DataSource archiveDataSource(
            @Qualifier("archiveDataSourceProperties") DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * ----------------------------------------------------------------
     * EntityManagerFactory Archive
     * ----------------------------------------------------------------
     * Configure Hibernate pour la base archive.
     * Scan le package "com.example.dualdatasource.entity.archive"
     * pour detecter les entites JPA.
     */
    @Bean(name = "archiveEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean archiveEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("archiveDataSource") DataSource dataSource) {

        // Proprietes specifiques Hibernate pour la base archive
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        return builder
                .dataSource(dataSource)
                .packages("com.tdsi.spring_multidatasource.entity.archive")
                .persistenceUnit("archive")
                .properties(properties)
                .build();
    }

    /**
     * ----------------------------------------------------------------
     * TransactionManager Archive
     * ----------------------------------------------------------------
     * Gestionnaire de transactions dédié pour la base archive.
     * Utilisation : @Transactional("archiveTransactionManager")
     */
    @Bean(name = "archiveTransactionManager")
    public PlatformTransactionManager archiveTransactionManager(
            @Qualifier("archiveEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
