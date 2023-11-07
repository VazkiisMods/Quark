package vazkii.quark.mixinsupport.delegates;

import net.minecraft.world.level.block.state.BlockState;
import vazkii.zeta.block.ext.CustomWeatheringCopper;
import vazkii.quark.mixinsupport.DelegateInterfaceTarget;
import vazkii.quark.mixinsupport.DelegateReturnValueTarget;

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
