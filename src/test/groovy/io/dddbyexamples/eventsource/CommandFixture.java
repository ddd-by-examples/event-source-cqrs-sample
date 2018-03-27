package io.dddbyexamples.eventsource;


import io.dddbyexamples.eventsource.domain.shopitem.commands.Buy;
import io.dddbyexamples.eventsource.domain.shopitem.commands.MarkPaymentTimeout;
import io.dddbyexamples.eventsource.domain.shopitem.commands.Pay;

import java.time.Instant;
import java.util.UUID;

import static java.math.BigDecimal.ZERO;
import static java.time.Instant.now;

public class CommandFixture {

    public static Buy buyItemCommand(UUID uuid) {
        return new Buy(uuid, ZERO, now());
    }

    public static Buy buyItemCommand(UUID uuid, Instant when) {
        return new Buy(uuid, ZERO, when);
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
