package bca.mbb.config;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Properties;

@Configuration
public class SCFKafkaProducer<V> {

    private final HashMap<String, KafkaProducer<String, V>> producers = new HashMap<>();
    private final SCFKafkaProducerConfig producerProperties;
    private final KafkaProducer<String, V> stringProducer;
    private final KafkaProducer<String, V> byteArrayProducer;
    private final KafkaProducer<String, V> objectProducer;

    public SCFKafkaProducer(SCFKafkaProducerConfig producerProperties) {
        this.producerProperties = producerProperties;
        this.stringProducer = createStringProducer();
        this.byteArrayProducer = createByteArrayProducer();
        this.objectProducer = createObjectProducer();
    }

    private KafkaProducer<String, V> createStringProducer() {
        Properties p = new Properties();
        p.putAll(this.producerProperties.getProperties());
        p.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        p.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(p);
    }

    private KafkaProducer<String, V> createByteArrayProducer() {
        Properties p = new Properties();
        p.putAll(this.producerProperties.getProperties());
        p.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        p.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        return new KafkaProducer<>(p);
    }

    private KafkaProducer<String, V> createObjectProducer() {
        Properties p = new Properties();
        p.putAll(this.producerProperties.getProperties());
        p.put("schema.registry.url", this.producerProperties.getSchemaRegistryUrl());
//        p.put("auto.register.schemas", this.producerProperties.getAutoRegisterSchema().toString());
//        p.put("ssl.client.auth", this.producerProperties.getSslClientAuth().toString());
//        p.put("schema.registry.ssl.truststore.location", this.producerProperties.getSslTrustStoreLocation());
//        p.put("schema.registry.ssl.truststore.password", this.producerProperties.getSslTrustStorePassword());
        p.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        p.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        return new KafkaProducer<>(p);
    }

    private KafkaProducer<String, V> getProducer(String topic, KafkaProducer<String, V> producer) {
        String key = this.producerProperties.getBrokerUrl().toUpperCase()
                + this.producerProperties.getPrincipal().toUpperCase()
                + this.producerProperties.getServiceName().toUpperCase()
                + topic.toUpperCase();

        producers.putIfAbsent(key, producer);
        return producers.get(key);
    }

    public KafkaProducerResponse sendMessage(String topic, V value) {
        KafkaProducer<String, V> producer;
        if (value.getClass().isAssignableFrom(String.class)) {
            producer = getProducer(topic, stringProducer);
        } else if (value.getClass().isAssignableFrom(byte[].class)) {
            producer = getProducer(topic, byteArrayProducer);
        } else {
            producer = getProducer(topic, objectProducer);
        }
        try {
            return new KafkaProducerResponse(true, null, producer.send(new ProducerRecord<>(topic, value)).get());
        } catch (Exception e) {
            e.printStackTrace();
            return new KafkaProducerResponse(false, e, null);
        } finally {
            producer.flush();
        }
    }
}
