package com.tdsi.spring_multidatasource.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
 * Configuration DataSource OPERATIONNEL (Principal)
 * ===================================================================
 * Cette classe configure :
 * - Le DataSource pour la base "operational"
 * - L'EntityManagerFactory dédié
 * - Le TransactionManager dédié
 * - Les repositories Spring Data JPA
 *
 * L'annotation @Primary indique que c'est la datasource par defaut
 * lorsqu'aucun qualifier n'est specifie.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        // Package contenant les repositories de la base operational
        basePackages = "com.tdsi.spring_multidatasource.repository.operational",
        // EntityManagerFactory dédié
        entityManagerFactoryRef = "operationalEntityManagerFactory",
        // TransactionManager dédié
        transactionManagerRef = "operationalTransactionManager"
)
public class OperationalDataSourceConfig {

    /**
     * ----------------------------------------------------------------
     * Properties du DataSource Operational
     * ----------------------------------------------------------------
     * Charge les proprietes depuis le prefixe "spring.datasource.operational"
     * dans le fichier application.yml
     */
    @Bean(name = "operationalDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.operational")
    @Primary  // Marque ce bean comme prioritaire
    public DataSourceProperties operationalDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * ----------------------------------------------------------------
     * DataSource Operational (Pool de connexions)
     * ----------------------------------------------------------------
     * Cree le DataSource HikariCP pour la base operational.
     * @Primary : DataSource par defaut de l'application.
     */
    @Bean(name = "operationalDataSource")
    @Primary
    public DataSource operationalDataSource(
            @Qualifier("operationalDataSourceProperties") DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    /**
     * ----------------------------------------------------------------
     * EntityManagerFactory Operational
     * ----------------------------------------------------------------
     * Configure Hibernate pour la base operational.
     * Scan le package "com.example.dualdatasource.entity.operational"
     * pour detecter les entites JPA.
     */
    @Bean(name = "operationalEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean operationalEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("operationalDataSource") DataSource dataSource) {

        // Proprietes specifiques Hibernate pour la base operational
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        return builder
                .dataSource(dataSource)
                .packages("com.tdsi.spring_multidatasource.entity.operational")
                .persistenceUnit("operational")
                .properties(properties)
                .build();
    }

    /**
     * ----------------------------------------------------------------
     * TransactionManager Operational
     * ----------------------------------------------------------------
     * Gestionnaire de transactions dédié pour la base operational.
     * Permet l'utilisation de @Transactional avec ce DataSource.
     */
    @Bean(name = "operationalTransactionManager")
    @Primary
    public PlatformTransactionManager operationalTransactionManager(
            @Qualifier("operationalEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
