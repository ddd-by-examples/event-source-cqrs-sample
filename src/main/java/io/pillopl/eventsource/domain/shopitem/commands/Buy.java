package io.pillopl.eventsource.domain.shopitem.commands;

import io.pillopl.eventsource.domain.Command;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class Buy implements Command {

    private final UUID uuid;
    private final BigDecimal amount;
    private final Instant when;

}
