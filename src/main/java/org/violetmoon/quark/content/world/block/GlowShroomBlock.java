package org.violetmoon.quark.content.world.block;

import net.minecraft.core.BlockPos;
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
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class GlowShroomBlock extends QuarkBushBlock implements BonemealableBlock {

	protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

	public GlowShroomBlock(ZetaModule module) {
		super("glow_shroom", module, CreativeModeTab.TAB_DECORATIONS,
				Properties.copy(Blocks.RED_MUSHROOM)
				.randomTicks()
				.lightLevel(s -> 10));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level worldIn, @Nonnull BlockPos pos, @Nonnull RandomSource rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		if(rand.nextInt(12) == 0 && worldIn.getBlockState(pos.above()).isAir())
			worldIn.addParticle(ParticleTypes.END_ROD,
					pos.getX() + 0.4 + rand.nextDouble() * 0.2,
					pos.getY() + 0.5 + rand.nextDouble() * 0.1,
					pos.getZ() + 0.4 + rand.nextDouble() * 0.2,
					(Math.random() - 0.5) * 0.04,
					(1 + Math.random()) * 0.02,
					(Math.random() - 0.5) * 0.04);
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos) {
		return state.getBlock() == Blocks.DEEPSLATE;
	}

	@Override
	public boolean isValidBonemealTarget(@Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(@Nonnull Level world, RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		return random.nextFloat() < 0.4D;
	}

	@Override
	public void performBonemeal(@Nonnull ServerLevel world, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		HugeGlowShroomBlock.place(world, random, pos);
	}

}
