package io.pillopl.eventsource.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.pillopl.eventsource.domain.shopitem.events.ItemBought;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout;

import java.time.Instant;
import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(name = ItemBought.TYPE, value = ItemBought.class),
        @JsonSubTypes.Type(name = ItemPaymentTimeout.TYPE, value = ItemPaymentTimeout.class),
        @JsonSubTypes.Type(name = ItemPaid.TYPE, value = ItemPaid.class)
})
public interface DomainEvent {

    String type();
    Instant when();
    UUID uuid();
}

