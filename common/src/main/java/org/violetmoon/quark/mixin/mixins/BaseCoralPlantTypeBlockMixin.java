package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.tweaks.module.CoralOnCactusModule;

@Mixin(BaseCoralPlantTypeBlock.class)
public class BaseCoralPlantTypeBlockMixin {

	@ModifyReturnValue(method = "scanForWater", at = @At("RETURN"))
	private static boolean scanForWater(boolean prev, BlockState state, BlockGetter getter, BlockPos pos) {
		return CoralOnCactusModule.scanForWater(state, getter, pos, prev);
	}

	@ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
	private boolean canSurvive(boolean prev, BlockState state, LevelReader getter, BlockPos pos) {
		return CoralOnCactusModule.scanForWater(state, getter, pos, prev);
	}

}
