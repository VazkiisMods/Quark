package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.HugeRedMushroomFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import org.violetmoon.quark.content.experimental.module.GameNerfsModule;

@Mixin(value = HugeRedMushroomFeature.class)
public class HugeRedMushroomFeatureMixin {

	@WrapWithCondition(
		method = "makeCap", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/feature/HugeRedMushroomFeature;setBlock(Lnet/minecraft/world/level/LevelWriter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
		)
	)
	public boolean isValidRepairItem(HugeRedMushroomFeature instance, LevelWriter level, BlockPos pos, BlockState stateToSet) {
		return !GameNerfsModule.shouldMushroomsUseTreeReplacementLogic() || TreeFeature.isAirOrLeaves((LevelAccessor) level, pos);
	}

}
