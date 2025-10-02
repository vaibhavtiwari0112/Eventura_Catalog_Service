package com.eventura.catalog.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class SeatLayoutMapper {

    private final ObjectMapper objectMapper;

    public SeatLayoutMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode toJsonNode(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON for seatLayout: " + json, e);
        }
    }

    public String toJsonString(JsonNode node) {
        try {
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting seatLayout to String", e);
        }
    }
}
