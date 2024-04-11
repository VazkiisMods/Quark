package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.violetmoon.quark.content.tools.module.TorchArrowModule;

@Mixin({ CrossbowItem.class })
public abstract class CrossbowMultishotMixin {

	@ModifyVariable(method = "tryLoadProjectiles", at = @At("STORE"), ordinal = 1)
	private static int tryLoadProjectiles(int original, LivingEntity entity, ItemStack stack) {
		if(original > 1 && entity.getProjectile(stack).is(TorchArrowModule.ignoreMultishot))
			return 1;
		return original;
	}
}
