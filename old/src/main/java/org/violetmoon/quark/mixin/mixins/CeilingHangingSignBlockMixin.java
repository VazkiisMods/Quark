package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.building.module.WoodenPostsModule;

@Mixin(CeilingHangingSignBlock.class)
public class CeilingHangingSignBlockMixin {

	@ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
	private boolean canSurvive(boolean prev, BlockState state, LevelReader worldIn, BlockPos pos) {
		return WoodenPostsModule.canHangingBlockConnect(state, worldIn, pos, prev);
	}

}
