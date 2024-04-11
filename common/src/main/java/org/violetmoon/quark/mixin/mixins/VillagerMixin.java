package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.entity.npc.Villager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.experimental.module.VillagerRerollingReworkModule;

@Mixin(Villager.class)
public class VillagerMixin {

	@Inject(method = "resetNumberOfRestocks", at = @At("TAIL"))
	public void resetRestocks(CallbackInfo ci) {
		Villager villager = (Villager) (Object) this;
		VillagerRerollingReworkModule.clearRerolls(villager);
		if(!VillagerRerollingReworkModule.rerollOnAnyRestock)
			VillagerRerollingReworkModule.attemptToReroll(villager);
	}

	@Inject(method = "restock", at = @At("TAIL"))
	public void restock(CallbackInfo ci) {
		Villager villager = (Villager) (Object) this;
		if(VillagerRerollingReworkModule.rerollOnAnyRestock)
			VillagerRerollingReworkModule.attemptToReroll(villager);
	}
}
