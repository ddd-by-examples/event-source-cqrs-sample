package io.dddbyexamples.eventsource.integration.eventstore

import io.dddbyexamples.eventsource.domain.shopitem.ShopItem
import io.dddbyexamples.eventsource.eventstore.EventSourcedShopItemRepository
import io.dddbyexamples.eventsource.integration.IntegrationSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.Instant

import static io.dddbyexamples.eventsource.ShopItemFixture.initialized
import static io.dddbyexamples.eventsource.domain.shopitem.ShopItemState.BOUGHT
import static io.dddbyexamples.eventsource.domain.shopitem.ShopItemState.PAID
import static java.time.LocalDate.now
import static java.time.ZoneId.systemDefault
import static java.time.temporal.ChronoUnit.DAYS

class EventSourcedShopItemRepositoryIntegrationSpec extends IntegrationSpec {

    private static final UUID uuid = UUID.randomUUID()
    private static final int PAYMENT_DEADLINE_IN_HOURS = 48
    private static final Instant TODAY = now().atStartOfDay(systemDefault()).toInstant()
    private static final Instant TOMORROW = TODAY.plus(1, DAYS)
    private static final Instant DAY_AFTER_TOMORROW =  TOMORROW.plus(1, DAYS)


    @Subject
    @Autowired
    EventSourcedShopItemRepository shopItemRepository

    def 'should store and load item'() {
        given:
            ShopItem stored = initialized().buy(uuid, TODAY, PAYMENT_DEADLINE_IN_HOURS)
        when:
            shopItemRepository.save(stored)
        and:
            ShopItem loaded = shopItemRepository.getByUUID(uuid)
        then:
            loaded.uuid == stored.uuid
            loaded.state == stored.state
    }

    def 'should reconstruct item at given moment'() {
        given:
            ShopItem stored = initialized()
                    .buy(uuid, TOMORROW, PAYMENT_DEADLINE_IN_HOURS)
                    .pay(DAY_AFTER_TOMORROW)
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
