package bca.mbb.config;

import bca.mbb.scf.avro.AuthorizeUploadData;
import bca.mbb.scf.avro.NotificationData;
import com.mybcabisnis.approvalworkflowbulk.kafka.avro.ApprovalStatusBulk;
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
    private final String schemaRegistryUrl;
    private final String concurrency;
    @Value(value = "${spring.kafka.consumer.group-id-notification}")
    private String groupIdNotification;

    @Value(value = "${spring.kafka.consumer.group-id-foundation-upload-release-bulk}")
    private String groupFoundationUploadReleaseBulk;

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
    public ConcurrentKafkaListenerContainerFactory<String, ApprovalStatusBulk> foundationUploadReleaseBulk() {
        ConcurrentKafkaListenerContainerFactory<String, ApprovalStatusBulk> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(groupFoundationUploadReleaseBulk));
        factory.setConcurrency(Integer.parseInt(concurrency));
        return factory;
    }

    private ConsumerFactory<String, Object> consumerFactory(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put("schema.registry.url", schemaRegistryUrl);
        props.put("auto.register.schemas", "false");
        props.put("specific.avro.reader", "true");
        props.put("use.latest.version", "true");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        return new DefaultKafkaConsumerFactory<>(props);
    }

}