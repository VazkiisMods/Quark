package org.violetmoon.quark.mixin.mixins.accessor;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractArrow.class)
public interface AccessorAbstractArrow {

	@Invoker("getPickupItem")
	ItemStack quark$getPickupItem();

}
