package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.level.block.SpongeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.tweaks.module.ImprovedSpongesModule;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {

	@ModifyExpressionValue(method = "removeWaterBreadthFirstSearch", at = @At(value = "CONSTANT", args = "intValue=65"))
	public int getDrainLimit(int original) {
		return ImprovedSpongesModule.drainLimit(original);
	}

	@ModifyExpressionValue(method = "removeWaterBreadthFirstSearch", at = @At(value = "CONSTANT", args = "intValue=6"))
	public int getCrawlLimit(int original) {
		return ImprovedSpongesModule.crawlLimit(original);
	}

}
