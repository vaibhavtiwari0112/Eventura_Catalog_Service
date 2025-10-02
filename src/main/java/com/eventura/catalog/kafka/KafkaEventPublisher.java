package com.eventura.catalog.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


import java.util.Map;


@Service
public class KafkaEventPublisher {


    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;


    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               @Value("${catalog.kafka.topic:catalog-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }


    public void publish(String eventType, Map<String, Object> payload) {
        try {
            Map<String, Object> ev = Map.of(
                    "event_type", eventType,
                    "payload", payload,
                    "ts", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(ev);
            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
// log and swallow -- failing the write should not break the main flow
            e.printStackTrace();
        }
    }
}