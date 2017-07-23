package io.pillopl.eventsource.domain.shopitem.accumulators;

import io.pillopl.eventsource.domain.shopitem.ShopItem;
import org.javers.core.diff.Change;

import java.util.function.BiFunction;

/**
 * @author pawel szymczyk
 */
abstract class ChangeAccumulator implements BiFunction<Change, ShopItem, ShopItem> {

    public abstract boolean accept(Change change);

    public abstract ShopItem apply(ShopItem shopItem, Change change);

    @Override
    public ShopItem apply(Change change, ShopItem shopItem) {
        return accept(change) ? apply(shopItem, change) : shopItem;
    }
}
