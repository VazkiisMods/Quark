package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.violetmoon.zeta.block.ext.CustomWeatheringCopper;

import java.util.Optional;

@Mixin(LightningBolt.class)
public class LightningBoltMixin {
	// This mixin exists as a failsafe for if something goes wrong with WeatheringCopperMixin.

	@WrapOperation(
		method = "clearCopperOnLightningStrike",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopper;getFirst(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/state/BlockState;")
	)
	private static BlockState customCopperGetFirst(BlockState baseState, Operation<BlockState> original) {
		if (baseState.getBlock() instanceof CustomWeatheringCopper customCopper)
			return customCopper.getFirst(baseState);
		return original.call(baseState);
	}

	@WrapOperation(
		method = "randomStepCleaningCopper",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/WeatheringCopper;getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;")
	)
	private static Optional<BlockState> customCopperGetPrevious(BlockState baseState, Operation<Optional<BlockState>> original) {
		if (baseState.getBlock() instanceof CustomWeatheringCopper customCopper)
			return customCopper.getPrevious(baseState);
		return original.call(baseState);
	}

}
