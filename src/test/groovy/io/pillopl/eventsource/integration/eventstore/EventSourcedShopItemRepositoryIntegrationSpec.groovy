package io.pillopl.eventsource.integration.eventstore

import io.pillopl.eventsource.domain.shopitem.ShopItem
import io.pillopl.eventsource.domain.shopitem.ShopItemState
import io.pillopl.eventsource.eventstore.EventSourcedShopItemRepository
import io.pillopl.eventsource.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.LocalDateTime
import java.time.ZoneId

import static io.pillopl.eventsource.ShopItemFixture.initialized
import static io.pillopl.eventsource.domain.shopitem.ShopItemState.BOUGHT
import static io.pillopl.eventsource.domain.shopitem.ShopItemState.PAID
import static io.pillopl.eventsource.integration.MockClock.*

class EventSourcedShopItemRepositoryIntegrationSpec extends IntegrationSpec {

    private static final UUID uuid = UUID.randomUUID()
    private static final int PAYMENT_DEADLINE_IN_HOURS = 48

    @Subject
    @Autowired
    EventSourcedShopItemRepository shopItemRepository


    def 'should store and load item'() {
        given:
            ShopItem stored = initialized(uuid)
        when:
            stored = shopItemRepository.save(stored)
        and:
            stored = stored.buy(uuid, TODAY, PAYMENT_DEADLINE_IN_HOURS)
            shopItemRepository.save(stored)
        and:
            ShopItem loaded = shopItemRepository.getByUUID(uuid)
        then:
            loaded.uuid == stored.uuid
            loaded.state == stored.state
    }

    def 'should reconstruct item at given moment'() {
        given:
            mockClock.clear()
            mockClock.mock(TODAY)
            mockClock.mock(TOMORROW)
            mockClock.mock(DAY_AFTER_TOMORROW)

            ShopItem stored = new ShopItem(uuid, Collections.EMPTY_LIST, ShopItemState.INITIALIZED)
            stored = shopItemRepository.save(stored)
            stored = stored.buy(uuid, TOMORROW, PAYMENT_DEADLINE_IN_HOURS)
            stored = shopItemRepository.save(stored)
            stored = stored.pay()

        when:
            shopItemRepository.save(stored)
        and:
            ShopItem bought = shopItemRepository.getByUUIDat(uuid, TOMORROW)
            ShopItem paid = shopItemRepository.getByUUIDat(uuid, DAY_AFTER_TOMORROW)

        then:
            bought.state == BOUGHT
            paid.state == PAID
    }

}
