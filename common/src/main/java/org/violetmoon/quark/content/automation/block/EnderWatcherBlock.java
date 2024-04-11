package org.violetmoon.quark.content.automation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.violetmoon.quark.content.automation.block.be.EnderWatcherBlockEntity;
import org.violetmoon.quark.content.automation.module.EnderWatcherModule;
import org.violetmoon.zeta.block.ZetaBlock;
import org.violetmoon.zeta.module.ZetaModule;

public class EnderWatcherBlock extends ZetaBlock implements EntityBlock {

	public static final BooleanProperty WATCHED = BooleanProperty.create("watched");
	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public EnderWatcherBlock(@Nullable ZetaModule module) {
		super("ender_watcher", module,
				Block.Properties.of()
						.mapColor(MapColor.COLOR_GREEN)
						.strength(3F, 10F)
						.sound(SoundType.METAL));

		registerDefaultState(defaultBlockState().setValue(WATCHED, false).setValue(POWER, 0));

		if(module == null) //auto registration below this line
			return;
		setCreativeTab(CreativeModeTabs.REDSTONE_BLOCKS);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WATCHED, POWER);
	}

	@Override
	public boolean isSignalSource(@NotNull BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState blockState, @NotNull BlockGetter blockAccess, @NotNull BlockPos pos, @NotNull Direction side) {
		return blockState.getValue(POWER);
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new EnderWatcherBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level world, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return createTickerHelper(type, EnderWatcherModule.blockEntityType, EnderWatcherBlockEntity::tick);
	}

}
