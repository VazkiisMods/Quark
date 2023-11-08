package org.violetmoon.quark.content.world.block;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.base.block.QuarkBushBlock;
import org.violetmoon.quark.base.handler.MiscUtil;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class GlowLichenGrowthBlock extends QuarkBushBlock implements BonemealableBlock {

	protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

	public GlowLichenGrowthBlock(ZetaModule module) {
		super("glow_lichen_growth", module, CreativeModeTab.TAB_DECORATIONS,
				Properties.copy(Blocks.GLOW_LICHEN)
				.randomTicks()
				.lightLevel(s -> 8));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		// spreading
		for(int i = 0; i < 10; i++)
			worldIn.addParticle(ParticleTypes.MYCELIUM,
					pos.getX() + (Math.random() - 0.5) * 5 + 0.5,
					pos.getY() + (Math.random() - 0.5) * 8 + 0.5,
					pos.getZ() + (Math.random() - 0.5) * 5 + 0.5,
					0, 0, 0);

		// focused
		worldIn.addParticle(ParticleTypes.MYCELIUM,
				pos.getX() + (Math.random() - 0.5) * 0.4 + 0.5,
				pos.getY() + (Math.random() - 0.5) * 0.3 + 0.3,
				pos.getZ() + (Math.random() - 0.5) * 0.4 + 0.5,
				0, 0, 0);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos) {
		return state.isFaceSturdy(world, pos, Direction.UP);
	}

	@Override
	public boolean isValidBonemealTarget(@Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean idkmansomething) {
		for(Direction dir : MiscUtil.HORIZONTALS)
			if(canSpread(world, pos.relative(dir)))
				return true;

		return false;
	}

	@Override
	public boolean isBonemealSuccess(@Nonnull Level world, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(@Nonnull ServerLevel world, @Nonnull RandomSource rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		List<Direction> list = Lists.newArrayList(MiscUtil.HORIZONTALS);
		Collections.shuffle(list);
		for(Direction dir : list) {
			BlockPos offPos = pos.relative(dir);
			if(canSpread(world, offPos)) {
				world.setBlock(offPos, state, 3);
				return;
			}
		}
	}

	private boolean canSpread(BlockGetter world, BlockPos pos) {
		BlockPos below = pos.below();
		return world.getBlockState(pos).isAir() && mayPlaceOn(world.getBlockState(below), world, below);
	}

}
