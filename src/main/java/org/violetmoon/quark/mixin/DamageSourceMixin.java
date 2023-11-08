package org.violetmoon.quark.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.violetmoon.quark.content.tools.module.PickarangModule;

@Mixin(DamageSource.class)
public class DamageSourceMixin {

	@Inject(method = "playerAttack", at = @At("HEAD"), cancellable = true)
	private static void playerAttack(Player player, CallbackInfoReturnable<DamageSource> callbackInfoReturnable) {
		DamageSource damage = PickarangModule.createDamageSource(player);

		if(damage != null)
			callbackInfoReturnable.setReturnValue(damage);
	}
}
