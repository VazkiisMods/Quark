package org.violetmoon.quark.addons.oddities.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.violetmoon.quark.addons.oddities.block.be.CrateBlockEntity;
import org.violetmoon.quark.addons.oddities.module.CrateModule;
import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrateBlock extends QuarkBlock implements EntityBlock {

	public static final BooleanProperty PROPERTY_OPEN = BlockStateProperties.OPEN;

	public CrateBlock(ZetaModule module) {
		super("crate", module, CreativeModeTab.TAB_DECORATIONS, Properties.copy(Blocks.BARREL));
		registerDefaultState(stateDefinition.any().setValue(PROPERTY_OPEN, false));
	}

	@Override
	public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof CrateBlockEntity crate) {
			var crateHandler = crate.itemHandler();
			return (int) (Math.floor((crateHandler.displayTotal * 14.0) / crateHandler.getSlots()) + (crateHandler.displayTotal > 0 ? 1 : 0));
		}
		return 0;
	}

	@Nullable
	@Override
	public MenuProvider getMenuProvider(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);
		return be instanceof MenuProvider provider ? provider : null;
	}


	@Nonnull
	@Override
	public InteractionResult use(@Nonnull BlockState state, Level worldIn, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand handIn, @Nonnull BlockHitResult hit) {
		if(worldIn.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity be = worldIn.getBlockEntity(pos);
			if(be instanceof CrateBlockEntity crate) {
				if(player instanceof ServerPlayer serverPlayer)
					NetworkHooks.openScreen(serverPlayer, crate, pos);

				PiglinAi.angerNearbyPiglins(player, true);
			}

			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void setPlacedBy(@Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(stack.hasCustomHoverName()) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);
			if(tileentity instanceof CrateBlockEntity crate)
				crate.setCustomName(stack.getHoverName());
		}
	}

	@Override
	public void tick(@Nonnull BlockState state, ServerLevel worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		BlockEntity tileentity = worldIn.getBlockEntity(pos);
		if(tileentity instanceof CrateBlockEntity)
			((CrateBlockEntity)tileentity).crateTick();
	}

	@Override
	public void onRemove(BlockState state, @Nonnull Level worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
		if(!state.is(newState.getBlock())) {
			BlockEntity tileentity = worldIn.getBlockEntity(pos);

			if(tileentity instanceof CrateBlockEntity crate) {
				crate.spillTheTea();
			}
		}

		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PROPERTY_OPEN);
	}

	@Override
	public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
		return new CrateBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level world, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
		return createTickerHelper(type, CrateModule.blockEntityType, CrateBlockEntity::tick);
	}

}
