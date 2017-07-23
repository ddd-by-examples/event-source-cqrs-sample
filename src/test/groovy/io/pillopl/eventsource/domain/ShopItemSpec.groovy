package io.pillopl.eventsource.domain.shopitem

import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout
import spock.lang.Specification
import spock.lang.Unroll

import static io.pillopl.eventsource.ShopItemFixture.*
import static java.time.Instant.now
import static java.time.Instant.parse

@Unroll
class ShopItemSpec extends Specification {

    private static final int PAYMENT_DEADLINE_IN_HOURS = 48
    private final UUID uuid = UUID.randomUUID()

    def 'should emit item bought event when buying initialized item'() {
        when:
            ShopItem tx = initialized(uuid).buy(uuid, now(), PAYMENT_DEADLINE_IN_HOURS)
        then:
            tx.getUncommittedChanges().size() == 1
            tx.getUncommittedChanges().head().type() == ItemBought.TYPE
    }

    def 'should calculate #deadline when buying at #buyingAt and expiration in hours #expiresIn'() {
        when:
            ShopItem tx = initialized(uuid).buy(uuid, parse(buyingAt), expiresIn)
        then:
            ((ItemBought) tx.getUncommittedChanges().head()).paymentTimeoutDate == parse(deadline)
        where:
            buyingAt               | expiresIn || deadline
            "1995-10-23T10:12:35Z" | 0         || "1995-10-23T10:12:35Z"
            "1995-10-23T10:12:35Z" | 1         || "1995-10-23T11:12:35Z"
            "1995-10-23T10:12:35Z" | 2         || "1995-10-23T12:12:35Z"
            "1995-10-23T10:12:35Z" | 20        || "1995-10-24T06:12:35Z"
            "1995-10-23T10:12:35Z" | 24        || "1995-10-24T10:12:35Z"
            "1995-10-23T10:12:35Z" | 48        || "1995-10-25T10:12:35Z"
    }

    def 'Payment expiration date cannot be in the past'() {
        given:
            ShopItem tx = initialized(uuid)
        when:
            tx.buy(uuid, now(), -1)
        then:
            Exception e = thrown(IllegalArgumentException)
            e.message.contains("Payment timeout day is before buying date")
    }

    def 'buying an item should be idempotent'() {
        given:
            ShopItem tx = bought(uuid)
        when:
            tx.buy(uuid, now(), PAYMENT_DEADLINE_IN_HOURS)
        then:
            tx.getUncommittedChanges().isEmpty()
    }

    def 'cannot pay for just initialized item'() {
        given:
            ShopItem tx = initialized(uuid)
        when:
            tx.pay()
        then:
            thrown(IllegalStateException)
    }

    def 'cannot mark payment timeout when item just initialized'() {
        given:
            ShopItem tx = initialized(uuid)
        when:
            tx.markTimeout()
        then:
            thrown(IllegalStateException)
    }

    def 'should emit item paid event when paying for bought item'() {
        when:
            ShopItem tx = bought(uuid).pay()
        then:
            tx.getUncommittedChanges().size() == 1
            tx.getUncommittedChanges().head().type() == ItemPaid.TYPE
    }

    def 'paying for an item should be idempotent'() {
        given:
            ShopItem tx = paid(uuid)
        when:
            tx.pay()
        then:
            tx.getUncommittedChanges().isEmpty()
    }

    def 'should emit payment timeout event when marking item as payment missing'() {
        when:
            ShopItem tx = bought(uuid).markTimeout()
        then:
            tx.getUncommittedChanges().size() == 1
            tx.getUncommittedChanges().head().type() == ItemPaymentTimeout.TYPE
    }

    def 'marking payment timeout should be idempotent'() {
        when:
            ShopItem tx = withTimeout(uuid).markTimeout()
        then:
            tx.getUncommittedChanges().isEmpty()
    }

    def 'cannot mark payment missing when item already paid'() {
        when:
            paid(uuid).markTimeout()
        then:
            thrown(IllegalStateException)
    }

    def 'should emit item paid event when receiving missed payment'() {
        when:
            ShopItem tx = withTimeout(uuid).pay()
        then:
            tx.getUncommittedChanges().size() == 1
            tx.getUncommittedChanges().head().type() == ItemPaid.TYPE

    }

    def 'receiving payment after timeout should be idempotent'() {
        when:
            ShopItem tx = withTimeoutAndPaid(uuid).pay()
        then:
            tx.getUncommittedChanges().isEmpty()
    }

}
