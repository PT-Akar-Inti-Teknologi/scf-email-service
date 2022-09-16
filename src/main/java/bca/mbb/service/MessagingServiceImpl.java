package bca.mbb.service;


import bca.mbb.api.MessagingService;
import bca.mbb.config.KafkaProducerResponse;
import bca.mbb.config.SCFKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl<V> implements MessagingService<V> {
    private final SCFKafkaProducer<V> producer;
    public KafkaProducerResponse sendMessage(String topic, V value){
        return producer.sendMessage(topic, value);
    }
}

