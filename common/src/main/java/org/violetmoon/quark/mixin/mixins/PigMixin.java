package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.violetmoon.quark.content.tweaks.module.PigLittersModule;

@Mixin(Pig.class)
public class PigMixin {

	@Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
	public void pigsEatGoldenCarrots(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if(PigLittersModule.canEat(stack))
			cir.setReturnValue(true);
	}

}
