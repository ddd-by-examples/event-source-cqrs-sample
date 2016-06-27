package io.pillopl.eventsource.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.pillopl.eventsource.domain.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
class EventSerializer {

    private final ObjectMapper objectMapper;

    @Autowired
    EventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    EventDescriptor serialize(DomainEvent event) {
        try {
            return new EventDescriptor(objectMapper.writeValueAsString(event), event.when(), event.type());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    DomainEvent deserialize(EventDescriptor eventDescriptor) {
        try {
            return objectMapper.readValue(eventDescriptor.getBody(), DomainEvent.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
