package io.pillopl.eventsource.domain.shopitem.accumulators;

import io.pillopl.eventsource.domain.shopitem.ShopItem;
import org.javers.core.diff.Change;

import java.util.Arrays;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ShopItemAccumulators {

    private static final List<ChangeAccumulator> ACCUMULATORS = Arrays.asList(
            new StateChangeAccumulator()
    );

    public static ShopItem reduce(ShopItem shopItem, Change change) {
        return ACCUMULATORS
                .stream()
                .reduce(
                        shopItem,
                        (tx, acc) -> acc.apply(change, tx),
                        (shopItem1, shopItem2) -> {throw new UnsupportedOperationException();}
                );
    }
}
