package io.pillopl.eventsource.integration.shopitem

import io.pillopl.eventsource.domain.shopitem.ShopItem
import io.pillopl.eventsource.domain.shopitem.ShopItems
import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout
import io.pillopl.eventsource.integration.IntegrationSpec
import io.pillopl.eventsource.store.EventStore
import io.pillopl.eventsource.store.EventStream
import io.pillopl.eventsource.store.ShopItemRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static io.pillopl.eventsource.CommandFixture.buyItemCommand
import static io.pillopl.eventsource.CommandFixture.markPaymentTimeoutCommand
import static io.pillopl.eventsource.CommandFixture.payItemCommand
import static io.pillopl.eventsource.domain.shopitem.ShopItemState.BOUGHT
import static io.pillopl.eventsource.domain.shopitem.ShopItemState.PAID
import static io.pillopl.eventsource.domain.shopitem.ShopItemState.PAYMENT_MISSING

class ShopItemsIntegrationSpec extends IntegrationSpec {

    private final UUID uuid = UUID.randomUUID()

    @Subject
    @Autowired
    ShopItems shopItems

    @Autowired
    ShopItemRepository repository

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

    def 'item should wait for payment when create bought item command comes and no item yet'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
        then:
            ShopItem tx = repository.getByUUID(uuid)
            tx.state == BOUGHT
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

    def 'item should be paid when paying for bought item'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = repository.getByUUID(uuid)
            tx.state == PAID
    }

    def 'cannot pay for not bought item'() {
        when:
            shopItems.pay(payItemCommand(uuid))
        then:
            Exception e = thrown(IllegalStateException)
            e.message.contains("Cannot pay")
            !eventStore.findByAggregateUUID(uuid).isPresent()
    }

    def 'item should be marked as payment timeout when payment did not come'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            ShopItem tx = repository.getByUUID(uuid)
            tx.state == PAYMENT_MISSING
    }

    def 'cannot mark payment missing when item already paid'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            Exception e = thrown(IllegalStateException)
            e.message.contains("Item already paid")
    }

    def 'cannot mark payment as missing when no item at all'() {
        when:
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            Exception e = thrown(IllegalStateException)
            e.message.contains("Payment is not missing yet")
            !eventStore.findByAggregateUUID(uuid).isPresent()
    }

    def 'should emit item paid event when receiving missed payment'() {
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

    def 'item should be paid when receiving missed payment'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = repository.getByUUID(uuid)
            tx.state == PAID

    }

    def 'buying an item should be idempotent'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.buy(buyItemCommand(uuid))
        then:
            ShopItem tx = repository.getByUUID(uuid)
            tx.state == BOUGHT
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE]

    }

    def 'marking payment as missing should be idempotent'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            ShopItem tx = repository.getByUUID(uuid)
            tx.state == PAYMENT_MISSING
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE, ItemPaymentTimeout.TYPE]
    }

    def 'paying should be idempotent'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = repository.getByUUID(uuid)
            tx.state == PAID
            Optional<EventStream> eventStream = eventStore.findByAggregateUUID(uuid)
            eventStream.isPresent()
            eventStream.get().getEvents()*.type == [ItemBought.TYPE, ItemPaid.TYPE]
    }

}
