package io.pillopl.eventsource.integration.boundary

import io.pillopl.eventsource.boundary.ShopItems
import io.pillopl.eventsource.domain.shopitem.ShopItem
import io.pillopl.eventsource.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static io.pillopl.eventsource.CommandFixture.*
import static io.pillopl.eventsource.domain.shopitem.ShopItemState.*

class ShopItemsIntegrationSpec extends IntegrationSpec {

    private final UUID uuid = UUID.randomUUID()

    @Subject
    @Autowired
    ShopItems shopItems

    def 'item should wait for payment when create bought item command comes and no item yet'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.state == BOUGHT
    }

    def 'item should be paid when paying for bought item'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.state == PAID
    }

    def 'cannot pay for not bought item'() {
        when:
            shopItems.pay(payItemCommand(uuid))
        then:
            Exception e = thrown(IllegalStateException)
            e.message.contains("Cannot pay")
    }

    def 'item should be marked as payment timeout when payment did not come'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
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
    }

    def 'item should be paid when receiving missed payment'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.state == PAID
    }

    def 'buying an item should be idempotent'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.buy(buyItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.state == BOUGHT
    }

    def 'marking payment as missing should be idempotent'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
            shopItems.markPaymentTimeout(markPaymentTimeoutCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.state == PAYMENT_MISSING
    }

    def 'paying should be idempotent'() {
        when:
            shopItems.buy(buyItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
            shopItems.pay(payItemCommand(uuid))
        then:
            ShopItem tx = shopItems.getByUUID(uuid)
            tx.state == PAID
    }

}
