package io.pillopl.eventsource.integration.eventstore

import io.pillopl.eventsource.domain.shopitem.ShopItem
import io.pillopl.eventsource.eventstore.EventSourcedShopItemRepository
import io.pillopl.eventsource.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static io.pillopl.eventsource.ShopItemFixture.initialized
import static java.time.Instant.now

class EventSourcedShopItemRepositoryIntegrationSpec extends IntegrationSpec {

    private static UUID uuid = UUID.randomUUID()
    private static final int PAYMENT_DEADLINE_IN_HOURS = 48

    @Subject
    @Autowired
    EventSourcedShopItemRepository shopItemRepository

    def 'should store and load item'() {
        given:
            ShopItem stored = initialized().buy(uuid, now(), PAYMENT_DEADLINE_IN_HOURS)
        when:
            shopItemRepository.save(stored)
        and:
            ShopItem loaded = shopItemRepository.getByUUID(uuid)
        then:
            loaded.uuid == stored.uuid
            loaded.state == stored.state

    }

}
