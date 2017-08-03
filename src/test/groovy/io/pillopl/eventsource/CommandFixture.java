package io.pillopl.eventsource;


import io.pillopl.eventsource.domain.shopitem.commands.Buy;
import io.pillopl.eventsource.domain.shopitem.commands.MarkPaymentTimeout;
import io.pillopl.eventsource.domain.shopitem.commands.Pay;

import java.time.Instant;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static java.time.Instant.now;

public class CommandFixture {

    public static Buy buyItemCommand(UUID uuid) {
        return new Buy(uuid, ZERO);
    }

    public static Buy buyItemCommand(UUID uuid, Instant when) {
        return new Buy(uuid, ZERO);
    }

    public static Pay payItemCommand(UUID uuid) {
        return new Pay(uuid, now());
    }

    public static Pay payItemCommand(UUID uuid, Instant when) {
        return new Pay(uuid, when);
    }


    public static MarkPaymentTimeout markPaymentTimeoutCommand(UUID uuid) {
        return new MarkPaymentTimeout(uuid, now());
    }

    public static MarkPaymentTimeout markPaymentTimeoutCommand(UUID uuid, Instant when) {
        return new MarkPaymentTimeout(uuid, when);
    }
}
