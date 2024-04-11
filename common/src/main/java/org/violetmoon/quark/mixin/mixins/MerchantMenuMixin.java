package org.violetmoon.quark.mixin.mixins;

import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.tools.module.AncientTomesModule;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin {

	@Final
	@Shadow
	private MerchantContainer tradeContainer;

	@Inject(method = "tryMoveItems", at = @At("HEAD"))
	private void moveAncientTomeItems(int offerId, CallbackInfo ci) {
		MerchantMenu self = (MerchantMenu) (Object) this;
		if(self.getOffers().size() > offerId) {
			AncientTomesModule.moveVillagerItems(self, tradeContainer, self.getOffers().get(offerId));
		}
	}
}
