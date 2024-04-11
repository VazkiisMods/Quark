package org.violetmoon.quark.mixin.mixins.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.BannerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(BannerItem.class)
public class BannerItemMixin {

	@ModifyExpressionValue(method = "appendHoverTextFromBannerBlockEntityTag", at = @At(value = "CONSTANT", args = "intValue=6"))
	private static int getLimit(int original) {
		return MoreBannerLayersModule.getLimit(original);
	}

}
