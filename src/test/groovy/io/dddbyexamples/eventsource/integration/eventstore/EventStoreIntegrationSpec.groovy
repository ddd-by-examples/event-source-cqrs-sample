package io.dddbyexamples.eventsource.integration.eventstore

import io.dddbyexamples.eventsource.boundary.ShopItems
import io.dddbyexamples.eventsource.domain.shopitem.events.ItemBought
import io.dddbyexamples.eventsource.domain.shopitem.events.ItemPaid
import io.dddbyexamples.eventsource.domain.shopitem.events.ItemPaymentTimeout
import io.dddbyexamples.eventsource.eventstore.EventStore
import io.dddbyexamples.eventsource.eventstore.EventStream
import io.dddbyexamples.eventsource.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static io.dddbyexamples.eventsource.CommandFixture.*

class EventStoreIntegrationSpec extends IntegrationSpec {

    private final UUID uuid = UUID.randomUUID()

    @Subject
    @Autowired
    ShopItems shopItems

    @Autowired
    EventStore eventStore


    def 'should store item bought event when create bought item command comes and no item yet'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE]
    }

    def 'should store item paid event when paying for existing item'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE, ItemPaid.TYPE]
    }

    def 'should store item paid event when receiving missed payment'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type ==
                    [ItemBought.TYPE, ItemPaymentTimeout.TYPE, ItemPaid.TYPE]

    }

    def 'buying an item should be idempoten - should store only 1 event'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.buy(buyItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE]

    }

    def 'marking payment as missing should be idempotent - should store only 1 event'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE, ItemPaymentTimeout.TYPE]
    }

    def 'paying should be idempotent - - should store only 1 event'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE, ItemPaid.TYPE]
    }

}
