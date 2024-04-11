package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractChestedHorse.class)
public interface AccessorAbstractChestedHorse {

	@Invoker("playChestEquipsSound")
	void quark$playChestEquipsSound();

}
