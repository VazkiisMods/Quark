package org.violetmoon.quark.mixin.mixins.client.accessor;

import org.spongepowered.asm.mixin.Mixin;

// Simply used as a way of spelling "instanceof CustomCreativeSlot", since it isn't a public class (and I don't feel like ATing it)
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen$CustomCreativeSlot")
public interface AccessorCustomCreativeSlot {
	// Quack.
}
