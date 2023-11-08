package org.violetmoon.quark.content.automation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.content.automation.block.be.EnderWatcherBlockEntity;
import org.violetmoon.quark.content.automation.module.EnderWatcherModule;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class EnderWatcherBlock extends QuarkBlock implements EntityBlock {

	public static final BooleanProperty WATCHED = BooleanProperty.create("watched");
	public static final IntegerProperty POWER = BlockStateProperties.POWER;

	public EnderWatcherBlock(ZetaModule module) {
		super("ender_watcher", module, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.METAL, MaterialColor.COLOR_GREEN)
				.strength(3F, 10F)
				.sound(SoundType.METAL));

		registerDefaultState(defaultBlockState().setValue(WATCHED, false).setValue(POWER, 0));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(WATCHED, POWER);
	}

	@Override
	public boolean isSignalSource(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public int getSignal(BlockState blockState, @Nonnull BlockGetter blockAccess, @Nonnull BlockPos pos, @Nonnull Direction side) {
		return blockState.getValue(POWER);
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new EnderWatcherBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, EnderWatcherModule.blockEntityType, EnderWatcherBlockEntity::tick);
	}

}
