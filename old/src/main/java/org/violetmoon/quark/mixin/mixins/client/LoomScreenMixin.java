package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(LoomScreen.class)
public class LoomScreenMixin {

	@ModifyExpressionValue(method = "containerChanged", at = @At(value = "CONSTANT", args = "intValue=6"))
	private static int getLimit(int original) {
		return MoreBannerLayersModule.getLimit(original);
	}

}
