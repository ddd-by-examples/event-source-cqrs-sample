package io.pillopl.eventsource.domain.shopitem.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemBought implements DomainEvent {

    public static final String TYPE = "item.bought";

    private UUID uuid;
    private Instant paymentTimeoutDate;

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }
}
