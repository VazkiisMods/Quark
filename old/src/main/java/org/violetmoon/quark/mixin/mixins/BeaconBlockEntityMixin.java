package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.quark.content.tools.module.BeaconRedirectionModule;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

	// This captures the for loop inside tick that computes the beacon segments
	@ModifyExpressionValue(method = "tick", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0))
	private static int tick(int original, Level level, BlockPos pos, BlockState state, BeaconBlockEntity beacon) {
		return BeaconRedirectionModule.tickBeacon(beacon, original);
	}

}
