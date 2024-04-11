package org.violetmoon.quark.content.building.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.zeta.module.ZetaModule;

public class SoulFurnaceBlock extends VariantFurnaceBlock {

	public static final BooleanProperty SOUL = BooleanProperty.create("soul");

	public SoulFurnaceBlock(String type, ZetaModule module, Properties props) {
		super(type, module, props);

		registerDefaultState(defaultBlockState().setValue(SOUL, false));
	}

	@Override
	public void animateTick(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		if(state.getValue(LIT) && state.getValue(SOUL)) {
			double d0 = (double) pos.getX() + 0.5D;
			double d1 = pos.getY();
			double d2 = (double) pos.getZ() + 0.5D;
			if(random.nextDouble() < 0.1D) {
				level.playLocalSound(d0, d1, d2, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}

			Direction direction = state.getValue(FACING);
			Direction.Axis direction$axis = direction.getAxis();
			double d4 = random.nextDouble() * 0.6D - 0.3D;
			double d5 = direction$axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52D : d4;
			double d6 = random.nextDouble() * 6.0D / 16.0D;
			double d7 = direction$axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52D : d4;
			level.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);

			return;
		}

		super.animateTick(state, level, pos, random);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockGetter iblockreader = context.getLevel();
		BlockPos blockpos = context.getClickedPos();
		BlockPos down = blockpos.below();
		BlockState downState = iblockreader.getBlockState(down);
		return super.getStateForPlacement(context).setValue(SOUL, downState.is(BlockTags.SOUL_FIRE_BASE_BLOCKS));
	}

	@NotNull
	@Override
	public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
		if(facing == Direction.DOWN)
			return stateIn.setValue(SOUL, facingState.is(BlockTags.SOUL_FIRE_BASE_BLOCKS));

		return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SOUL);
	}

}
