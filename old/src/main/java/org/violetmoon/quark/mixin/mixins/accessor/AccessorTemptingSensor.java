package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.entity.ai.sensing.TemptingSensor;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TemptingSensor.class)
public interface AccessorTemptingSensor {
	@Accessor("temptations")
	Ingredient quark$getTemptations();
}
