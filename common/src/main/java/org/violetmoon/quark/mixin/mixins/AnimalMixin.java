package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.tweaks.module.PigLittersModule;

@Mixin(Animal.class)
public class AnimalMixin {

	@Inject(method = "usePlayerItem", at = @At("HEAD"))
	public void onEatGoldenCarrot(Player player, InteractionHand hand, ItemStack stack, CallbackInfo ci) {
		PigLittersModule.onEat((Animal) (Object) this, stack);
	}

}
