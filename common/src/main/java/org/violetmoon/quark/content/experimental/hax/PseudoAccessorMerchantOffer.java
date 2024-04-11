package org.violetmoon.quark.content.experimental.hax;

import org.violetmoon.quark.mixin.mixins.MerchantOfferMixin;

/**
 * This is extremely jank. Please do not use this as an example for anything unless you ABSOLUTELY know what you're
 * doing.
 *
 * This class is implemented on {@link net.minecraft.world.item.trading.MerchantOffer} via
 * {@link MerchantOfferMixin}.
 * Therefore, an offer can be cast to this interface, and subsequently use the interface methods implemented in the
 * mixin.
 *
 * Names are prefixed with {@code quark$} to avoid clashing with other mixed-in methods.
 */
public interface PseudoAccessorMerchantOffer {

	int quark$getTier();

	void quark$setTier(int tier);
}
