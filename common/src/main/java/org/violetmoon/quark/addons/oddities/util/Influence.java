package org.violetmoon.quark.addons.oddities.util;

import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public record Influence(List<Enchantment> boost, List<Enchantment> dampen) {
}
