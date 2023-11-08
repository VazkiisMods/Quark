package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.violetmoon.zeta.block.ext.CustomWeatheringCopper;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import java.util.Optional;

public class WeatheringCopperVerticalSlabBlock extends QuarkVerticalSlabBlock implements CustomWeatheringCopper {
	private final WeatheringCopper.WeatherState weatherState;
	public WeatheringCopperVerticalSlabBlock first;
	public WeatheringCopperVerticalSlabBlock prev;
	public WeatheringCopperVerticalSlabBlock next;

	public WeatheringCopperVerticalSlabBlock(Block parent, ZetaModule module) {
		super(parent, module);
		weatherState = ((WeatheringCopper) parent).getAge();
	}

	@Override
	public void randomTick(@Nonnull BlockState state, @Nonnull ServerLevel world, @Nonnull BlockPos pos, @Nonnull RandomSource random) {
		this.onRandomTick(state, world, pos, random);
	}

	@Override
	public boolean isRandomlyTicking(@Nonnull BlockState state) {
		return getNext(state).isPresent();
	}

	@Nonnull
	@Override
	public Optional<BlockState> getNext(@Nonnull BlockState state) {
		return next == null ? Optional.empty() : Optional.of(next.withPropertiesOf(state));
	}

	@Nonnull
	@Override
	public Optional<BlockState> getPrevious(@Nonnull BlockState state) {
		return prev == null ? Optional.empty() : Optional.of(prev.withPropertiesOf(state));
	}

	@Nonnull
	@Override
	public BlockState getFirst(@Nonnull BlockState state) {
		return first.withPropertiesOf(state);
	}

	@Nonnull
	@Override
	public WeatheringCopper.WeatherState getAge() {
		return weatherState;
	}

}
