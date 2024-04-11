package org.violetmoon.quark.content.client.hax;

import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import org.violetmoon.quark.content.client.resources.AttributeSlot;
import org.violetmoon.quark.mixin.mixins.ItemStackMixin;

import java.util.List;
import java.util.Map;

/**
 * This is extremely jank. Please do not use this as an example for anything unless you ABSOLUTELY know what you're
 * doing.
 *
 * This class is implemented on {@link net.minecraft.world.item.ItemStack} via
 * {@link ItemStackMixin}.
 * Therefore, a stack can be cast to this interface, and subsequently use the interface methods implemented in the
 * mixin.
 *
 * Names are prefixed with {@code quark$} to avoid clashing with other mixed-in methods.
 */
public interface PseudoAccessorItemStack {

	Map<AttributeSlot, Multimap<Attribute, AttributeModifier>> quark$getCapturedAttributes();

	void quark$capturePotionAttributes(List<Pair<Attribute, AttributeModifier>> attributes);
}
