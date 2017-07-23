package io.pillopl.eventsource.eventstore;

import lombok.Getter;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "event_descriptors")
class EventDescriptor {

    @Id
    @GeneratedValue(generator = "event_descriptors_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "event_descriptors_seq", sequenceName = "event_descriptors_seq", allocationSize = 1)
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false, length = 600)
    private String body;

    @Getter
    @Column(nullable = false, length = 60)
    private String type;

    EventDescriptor(String body, String type) {
        this.body = body;
        this.type = type;
    }

    private EventDescriptor() {
    }
}
