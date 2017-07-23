package io.pillopl.eventsource.eventstore;


import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.shopitem.ShopItemRepository;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.annotation.JaversAuditable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class EventSourcedShopItemRepository implements ShopItemRepository {

    private final Javers javers;

    @Autowired
    public EventSourcedShopItemRepository(Javers javers) {
        this.javers = javers;
    }

    @JaversAuditable
    @Override
    public ShopItem save(ShopItem aggregate) {
        return aggregate;
    }

    @Override
    public ShopItem getByUUID(UUID uuid) {
        return ShopItem.from(uuid, getRelatedEvents(uuid));
    }

    @Override
    public ShopItem getByUUIDat(UUID uuid, Instant at) {
        return ShopItem.from(uuid, getRelatedEvents(uuid, at));
    }

    private List<Change> getRelatedEvents(UUID uuid, Instant at) {
        return reverse(
                javers.findChanges(QueryBuilder.byInstanceId(uuid, ShopItem.class)
                        .to(LocalDateTime.ofInstant(at, ZoneId.systemDefault()))
                        .build()
                )
        );
    }

    private List<Change> getRelatedEvents(UUID uuid) {
        return reverse(
                javers.findChanges(QueryBuilder.byInstanceId(uuid, ShopItem.class)
                        .build()
                )
        );
    }

    private List<Change> reverse(List<Change> changes) {
        Collections.reverse(changes);
        return changes;
    }
}
