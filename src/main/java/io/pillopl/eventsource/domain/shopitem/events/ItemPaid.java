package io.pillopl.eventsource.domain.shopitem.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemPaid implements DomainEvent {

    public static final String TYPE = "item.paid";

    private UUID uuid;

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public UUID uuid() {
        return uuid;
    }
}
