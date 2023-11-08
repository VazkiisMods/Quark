package org.violetmoon.zeta.mixin.plugin.delegates;

import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.block.ext.CustomWeatheringCopper;
import org.violetmoon.zeta.mixin.plugin.DelegateInterfaceTarget;
import org.violetmoon.zeta.mixin.plugin.DelegateReturnValueTarget;

import java.util.Optional;

@DelegateInterfaceTarget
public class WeatheringCopperDelegate {

	@DelegateReturnValueTarget("getPrevious(Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/Optional;")
	public static Optional<BlockState> customWeatheringPrevious(Optional<BlockState> original, BlockState state) {
		if (state.getBlock() instanceof CustomWeatheringCopper copper)
			return copper.getPrevious(state);
		return original;
	}

	@DelegateReturnValueTarget("getFirst(Lnet/minecraft/world/level/block/state/BlockState;)Ljnet/minecraft/world/level/block/state/BlockState;")
	public static BlockState customWeatheringFirst(BlockState original, BlockState state) {
		if (state.getBlock() instanceof CustomWeatheringCopper copper)
			return copper.getFirst(state);
		return original;
	}
}
