package bca.mbb.config;

import bca.mbb.scf.avro.AuthorizeUploadData;
import bca.mbb.scf.avro.NotificationData;
import bca.mbb.scf.avro.TransactionData;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class SCFKafkaConsumerConfig {

    private final String brokerUrl;
    private final String keyTabFileLocation;
    private final String krb5ConfigFileLocation;
    private final String principalName;
    private final String serviceName;
    private final String sslTrustStoreLocation;
    private final String sslTrustStorePassword;
    private final String schemaRegistryUrl;
    private final String concurrency;
    @Value(value = "${spring.kafka.consumer.group-id-notification}")
    private String groupIdNotification;
    @Value(value = "${spring.kafka.consumer.group-id-transaction}")
    private String groupIdTransaction;

    public SCFKafkaConsumerConfig(
            @Value("${confluent.broker.list}") final String brokerUrl,
            @Value("${confluent.kerberos.keytab.location}") final String keyTabFileLocation,
            @Value("${confluent.kerberos.config.file}") final String krb5ConfigFileLocation,
            @Value("${confluent.kerberos.principal}") final String principalName,
            @Value("${confluent.kerberos.servicename}") final String serviceName,
            @Value("${confluent.schema.registry.truststore}") final String sslTrustStoreLocation,
            @Value("${confluent.schema.registry.truststore.password}") final String sslTrustStorePassword,
            @Value("${confluent.schema.registry}") final String schemaRegistryUrl,
            @Value("${confluent.concurrency}") final String concurrency
    ) {
        this.brokerUrl = brokerUrl;
        this.keyTabFileLocation = keyTabFileLocation;
        this.krb5ConfigFileLocation = krb5ConfigFileLocation;
        this.principalName = principalName;
        this.serviceName = serviceName;
        this.sslTrustStoreLocation = sslTrustStoreLocation;
        this.sslTrustStorePassword = sslTrustStorePassword;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.concurrency = concurrency;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, NotificationData> validateDoneListener() {
        ConcurrentKafkaListenerContainerFactory<String, NotificationData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(groupIdNotification));
        factory.setConcurrency(Integer.parseInt(concurrency));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthorizeUploadData> uploadValidatedInvoiceListener() {
        ConcurrentKafkaListenerContainerFactory<String, AuthorizeUploadData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(groupIdNotification));
        factory.setConcurrency(Integer.parseInt(concurrency));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionData> channelSynchronizerListener() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(groupIdTransaction));
        factory.setConcurrency(Integer.parseInt(concurrency));
        return factory;
    }

    private ConsumerFactory<String, Object> consumerFactory(String groupId) {
        // enable debug for kerberos
        // System.setProperty("sun.security.krb5.debug", "true");

        Map<String, Object> props = new HashMap<>();
        // kafka instance url
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);

        // <K,V> -> define tipe data K dan V nya
        // perlu ada perjanjian antara producer dan consumer untuk tipe datanya
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("auto.register.schemas", "false");
        props.put("specific.avro.reader", "true");
        props.put("use.latest.version", "true");


        // Apabila dibutuhkan setup grup id pada consumer factory
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // define protocolnya as https/ssl dan konfigurasi security protocol-nya
//        props.put("security.protocol", "SASL_SSL");
//        props.put("sasl.mechanism", "GSSAPI");
//        props.put("ssl.client.auth", "true");
//        props.put("schema.registry.ssl.truststore.location", sslTrustStoreLocation);
//        props.put("schema.registry.ssl.truststore.password", sslTrustStorePassword);
//        props.put("ssl.truststore.location", sslTrustStoreLocation);
//        props.put("ssl.truststore.password", sslTrustStorePassword);

        // konfigurasi security kerberos beserta username dan password
//        String jaasTemplate = "com.sun.security.auth.module.Krb5LoginModule required doNotPrompt=true useKeyTab=true "
//                + "storeKey=false useTicketCache=false serviceName=\"%s\" keyTab=\"%s\" principal=\"%s\";";
//        String jaasCfg = String.format(jaasTemplate, serviceName, keyTabFileLocation, principalName);
//        props.put("sasl.jaas.config", jaasCfg);
//        props.put("sasl.kerberos.service.name", serviceName);
//
//        // konfigurasi untuk mengoverride file krb5.conf yang ada di server
//        if (!(krb5ConfigFileLocation == null || krb5ConfigFileLocation.trim().isEmpty())) {
//            System.setProperty("java.security.krb5.conf", krb5ConfigFileLocation);
//        }

        return new DefaultKafkaConsumerFactory<>(props);
    }

//    private Map<String, Object> getProps(){
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
//
//        props.put("security.protocol", "SASL_SSL");
//        props.put("sasl.mechanism", "GSSAPI");
//        props.put("ssl.client.auth", "true");
//        props.put("schema.registry.ssl.truststore.location", sslTrustStoreLocation);
//        props.put("schema.registry.ssl.truststore.password", sslTrustStorePassword);
//        props.put("ssl.truststore.location", sslTrustStoreLocation);
//        props.put("ssl.truststore.password", sslTrustStorePassword);
//        String jaasTemplate = "com.sun.security.auth.module.Krb5LoginModule required doNotPrompt=true useKeyTab=true storeKey=false useTicketCache=false serviceName=\"%s\" keyTab=\"%s\" principal=\"%s\";";
//        props.put("sasl.jaas.config", String.format(jaasTemplate, serviceName, keyTabFileLocation, principalName));
//        props.put("sasl.kerberos.service.name", serviceName);
//        props.put("schema.registry.url", schemaRegistryUrl);
//        props.put("auto.register.schemas", "false");
//        props.put("specific.avro.reader", "true");
//        props.put("use.latest.version", "true");
//
//        if (!(krb5ConfigFileLocation == null || krb5ConfigFileLocation.trim().isEmpty())) {
//            System.setProperty("java.security.krb5.conf", krb5ConfigFileLocation);
//        }
//
//        return props;
//    }
}