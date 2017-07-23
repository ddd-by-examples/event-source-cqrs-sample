package io.pillopl.eventsource.readmodel

import io.pillopl.eventsource.domain.shopitem.events.ItemBought
import io.pillopl.eventsource.domain.shopitem.events.ItemPaid
import io.pillopl.eventsource.domain.shopitem.events.ItemPaymentTimeout
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant

import static java.time.Instant.now
import static java.util.UUID.randomUUID

class ReadModelPopulatorSpec extends Specification {

    private static final UUID ANY_UUID = randomUUID()
    private static final Instant ANY_PAYMENT_TIMEOUT = now()

    JdbcReadModel jdbcReadModel = Mock()

    @Subject
    ReadModelOnDomainEventUpdater ReadModelUpdater = new ReadModelOnDomainEventUpdater(jdbcReadModel)

    def 'should update or create bought item when receiving bought item event'() {
        when:
            ReadModelUpdater.handle(new ItemBought(ANY_UUID, ANY_PAYMENT_TIMEOUT))
        then:
            1 * jdbcReadModel.updateOrCreateItemAsBlocked(ANY_UUID, ANY_PAYMENT_TIMEOUT)
    }

    def 'should update item when receiving item paid event'() {
        when:
            ReadModelUpdater.handle(new ItemPaid(ANY_UUID))
        then:
            1 * jdbcReadModel.updateItemAsPaid(ANY_UUID)
    }

    def 'should update item when receiving payment timeout event'() {
        when:
            ReadModelUpdater.handle(new ItemPaymentTimeout(ANY_UUID))
        then:
            1 * jdbcReadModel.updateItemAsPaymentMissing(ANY_UUID)
    }
}
