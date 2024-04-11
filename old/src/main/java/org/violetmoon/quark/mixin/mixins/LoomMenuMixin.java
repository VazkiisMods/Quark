package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.inventory.LoomMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(LoomMenu.class)
public class LoomMenuMixin {

	@ModifyExpressionValue(method = "slotsChanged", at = @At(value = "CONSTANT", args = "intValue=6"))
	public int getLimit(int original) {
		return MoreBannerLayersModule.getLimit(original);
	}

}
