package bca.mbb.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(
        basePackages = {"bca.mbb"},
        entityManagerFactoryRef = "oracleEntityManager",
        transactionManagerRef = "oracleTransactionManager"
)
public class EntityConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean oracleEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(oracleDataSource());
        em.setPackagesToScan(new String[] {"lib.fo.entity"});

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("spring.jpa.database-platform", env.getProperty("spring.jpa.database-platform"));
        properties.put("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults", env.getProperty("spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults"));
        properties.put("spring.jpa.hibernate.use-new-id-generator-mappings", env.getProperty("spring.jpa.hibernate.use-new-id-generator-mappings"));
        properties.put("spring.jpa.show-sql", env.getProperty("spring.jpa.show-sql"));
        properties.put("spring.jpa.hibernate.ddl-auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("spring.jpa.properties.hibernate.order_updates", env.getProperty("spring.jpa.properties.hibernate.order_updates"));
        properties.put("spring.jpa.properties.hibernate.order_inserts", env.getProperty("spring.jpa.properties.hibernate.order_inserts"));
        properties.put("spring.jpa.properties.hibernate.batch_versioned_data", env.getProperty("spring.jpa.properties.hibernate.batch_versioned_data"));
        properties.put("spring.jpa.properties.hibernate.jdbc.batch_size", env.getProperty("spring.jpa.properties.hibernate.jdbc.batch_size"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public DataSource oracleDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager oracleTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(oracleEntityManager().getObject());
        return transactionManager;
    }
}
