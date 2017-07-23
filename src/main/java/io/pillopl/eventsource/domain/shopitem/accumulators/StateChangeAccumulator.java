package io.pillopl.eventsource.domain.shopitem.accumulators;

import io.pillopl.eventsource.domain.shopitem.ShopItem;
import io.pillopl.eventsource.domain.shopitem.ShopItemState;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.ValueChange;

/**
 * @author pawel szymczyk
 */
class StateChangeAccumulator extends ChangeAccumulator {
    @Override
    public boolean accept(Change change) {
        return change instanceof ValueChange && ((ValueChange) change).getRight() instanceof ShopItemState;
    }

    @Override
    public ShopItem apply(ShopItem shopItem, Change change) {
        return new ShopItem(shopItem.getUuid(), shopItem.getChanges(), (ShopItemState) ((ValueChange) change).getRight());
    }
}
