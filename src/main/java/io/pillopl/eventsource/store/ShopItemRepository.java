package io.pillopl.eventsource.store;


import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
public class ShopItemRepository {

    private final EventStore eventStore;
    private final EventSerializer eventSerializer;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public ShopItemRepository(EventStore eventStore, EventSerializer eventSerializer, ApplicationEventPublisher eventPublisher) {
        this.eventStore = eventStore;
        this.eventSerializer = eventSerializer;
        this.eventPublisher = eventPublisher;
    }

    public ShopItem save(ShopItem aggregate) {
        final List<DomainEvent> pendingEvents = aggregate.getUncommittedChanges();
        eventStore.saveEvents(
                aggregate.getUuid(),
                pendingEvents
                        .stream()
                        .map(eventSerializer::serialize)
                        .collect(toList()));
        pendingEvents.forEach(eventPublisher::publishEvent);
        return aggregate.markChangesAsCommitted();
    }

    public ShopItem getByUUID(UUID uuid) {
        List<DomainEvent> domainEvents =
                eventStore.getEventsForAggregate(uuid)
                        .stream()
                        .map(eventSerializer::deserialize)
                        .collect(toList());
        return ShopItem.from(uuid, domainEvents);
    }

}
