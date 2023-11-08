package org.violetmoon.quark.mixin.client;

import net.minecraft.world.item.BannerItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.violetmoon.quark.content.tweaks.module.MoreBannerLayersModule;

@Mixin(BannerItem.class)
public class BannerItemMixin {

	@ModifyConstant(method = "appendHoverTextFromBannerBlockEntityTag", constant = @Constant(intValue = 6))
	private static int getLimit(int curr) {
		return MoreBannerLayersModule.getLimit(curr);
	}
	
}
