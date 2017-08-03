package io.pillopl.eventsource.boundary;

import com.google.common.collect.ImmutableList;
import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.shopitem.ShopItemRepository;
import io.pillopl.eventsource.domain.shopitem.ShopItemState;
import io.pillopl.eventsource.domain.shopitem.commands.Buy;
import io.pillopl.eventsource.domain.shopitem.commands.MarkPaymentTimeout;
import io.pillopl.eventsource.domain.shopitem.commands.Pay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static io.pillopl.eventsource.domain.shopitem.ShopItemState.*;

@Service
@Transactional
@Slf4j
public class ShopItems {

    private final ShopItemRepository itemRepository;
    private final int hoursToPaymentTimeout;
    private final Clock clock;

    @Autowired
    public ShopItems(ShopItemRepository itemRepository,
                     @Value("${hours.to.payment.timeout:48}") int hoursToPaymentTimeout,
                     Clock clock) {
        this.itemRepository = itemRepository;
        this.hoursToPaymentTimeout = hoursToPaymentTimeout;
        this.clock = clock;
    }

    public void buy(Buy command) {
        itemRepository.save(new ShopItem(command.getUuid(), ImmutableList.of(), INITIALIZED));
        withItem(command.getUuid(), tx ->
                tx.buy(command.getUuid(), clock.instant(), hoursToPaymentTimeout)
        );
        log.info("{} item bought at {}", command.getUuid());
    }

    public void pay(Pay command) {
        withItem(command.getUuid(), tx ->
                tx.pay()
        );
        log.info("{} item paid at {}", command.getUuid(), command.getWhen());
    }

    public void markPaymentTimeout(MarkPaymentTimeout command) {
        withItem(command.getUuid(), tx ->
                tx.markTimeout()
        );
        log.info("{} item marked as payment timeout at {}", command.getUuid(), command.getWhen());
    }

    public ShopItem getByUUID(UUID uuid) {
        return itemRepository.getByUUID(uuid);
    }

    private ShopItem withItem(UUID uuid, UnaryOperator<ShopItem> action) {
        final ShopItem tx = getByUUID(uuid);
        final ShopItem modified = action.apply(tx);
        return itemRepository.save(modified);
    }
}
