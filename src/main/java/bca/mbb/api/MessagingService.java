package bca.mbb.api;

import bca.mbb.config.KafkaProducerResponse;

public interface MessagingService<V> {
    KafkaProducerResponse sendMessage(String topic, V value);
}