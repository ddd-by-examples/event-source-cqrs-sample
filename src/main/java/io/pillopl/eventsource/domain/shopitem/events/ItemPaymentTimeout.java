package io.pillopl.eventsource.domain.shopitem.events;

import io.pillopl.eventsource.domain.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPaymentTimeout implements DomainEvent {

    public static final String TYPE = "item.payment.timeout";

    private UUID uuid;
    private Instant when;

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Instant when() {
        return when;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }
}
