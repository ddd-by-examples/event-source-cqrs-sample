package io.pillopl.eventsource.domain.shopitem.commands;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class Buy implements Command {

    private final UUID uuid;
    private final BigDecimal amount;

}
