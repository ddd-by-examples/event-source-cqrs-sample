package io.pillopl.eventsource.readmodel;

import io.pillopl.eventsource.domain.shopitem.events.DomainEvent;
import io.pillopl.eventsource.domain.shopitem.events.ItemBought;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid;
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReadModelOnDomainEventUpdater {

    private final JdbcReadModel jdbcReadModelUpdater;

    @Autowired
    public ReadModelOnDomainEventUpdater(JdbcReadModel jdbcReadModelUpdater) {
        this.jdbcReadModelUpdater = jdbcReadModelUpdater;
    }

    @TransactionalEventListener
    public void handle(DomainEvent event) {
        if (event instanceof ItemBought) {
            final ItemBought itemBought = (ItemBought) event;
            jdbcReadModelUpdater.updateOrCreateItemAsBlocked(event.uuid(), itemBought.getPaymentTimeoutDate());
        } else if (event instanceof ItemPaid) {
            jdbcReadModelUpdater.updateItemAsPaid(event.uuid());
        } else if (event instanceof ItemPaymentTimeout) {
            jdbcReadModelUpdater.updateItemAsPaymentMissing(event.uuid());
        } else {
            throw new IllegalArgumentException("Cannot handle event " + event.getClass());
        }
    }

}
