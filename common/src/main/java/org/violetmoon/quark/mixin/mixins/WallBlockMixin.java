package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.building.module.VerticalSlabsModule;

@Mixin(WallBlock.class)
public class WallBlockMixin {

	@ModifyReturnValue(method = "connectsTo", at = @At("RETURN"))
	private boolean connectsTo(boolean prev, BlockState state, boolean sturdy, Direction dir) {
		return VerticalSlabsModule.shouldWallConnect(state, dir, prev);
	}

}
