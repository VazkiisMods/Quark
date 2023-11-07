package vazkii.zeta.block.ext;

import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface CustomWeatheringCopper extends WeatheringCopper {
	default BlockState getFirst(BlockState state) {
		return WeatheringCopper.getFirst(state.getBlock()).withPropertiesOf(state);
	}

	default Optional<BlockState> getPrevious(BlockState state) {
		return WeatheringCopper.getNext(state.getBlock()).map((prevBlock) -> prevBlock.withPropertiesOf(state));
	}
}
