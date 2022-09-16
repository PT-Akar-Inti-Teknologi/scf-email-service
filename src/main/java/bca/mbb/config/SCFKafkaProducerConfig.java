package bca.mbb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@ConfigurationProperties("middleware.kafkaproducer")
@Setter
public class SCFKafkaProducerConfig {
    @Getter
    private String brokerUrl;
    private String keyTabLocation;
    private String configFileLocation;
    @Getter
    private String principal;
    @Getter
    private String serviceName;
    @Getter
    private String sslTrustStoreLocation;
    @Getter
    private String sslTrustStorePassword;
    @Getter
    private String schemaRegistryUrl;
    private String requestTimeout = "25000";
    private String deliveryTimeout = "27000";
    private String maxBlock = "29000";
    private String securityProtocol = "SASL_SSL";
    private String saslMechanism = "GSSAPI";
    @Getter
    private Boolean autoRegisterSchema = false;
    @Getter
    private Boolean sslClientAuth = true;
    @Getter
    private Properties properties;

    @PostConstruct
    public void setUpProperties() {
        properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        properties.put(REQUEST_TIMEOUT_MS_CONFIG, requestTimeout); // in ms
        properties.put(DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout); // in ms
        properties.put(MAX_BLOCK_MS_CONFIG, maxBlock); // in ms

//        properties.put("security.protocol", securityProtocol);
//        properties.put("sasl.mechanism", saslMechanism);
//        properties.put("ssl.truststore.location", sslTrustStoreLocation);
//        properties.put("ssl.truststore.password", sslTrustStorePassword);
//        String jaasTemplate = "com.sun.security.auth.module.Krb5LoginModule required doNotPrompt=true useKeyTab=true "
//                + "storeKey=false useTicketCache=false keyTab=\"%s\" principal=\"%s\";";
//        String jaasCfg = String.format(jaasTemplate, keyTabLocation, principal);
//        properties.put("sasl.jaas.config", jaasCfg);
//        properties.put("sasl.kerberos.service.name", serviceName);
//        if (configFileLocation != null && !configFileLocation.trim().isEmpty()) {
//            System.setProperty("java.security.krb5.conf", configFileLocation);
//        }
    }
}
