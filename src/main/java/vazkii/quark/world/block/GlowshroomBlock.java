package vazkii.quark.world.block;

import java.util.Random;
import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.Module;
import vazkii.quark.world.module.underground.GlowshroomUndergroundBiomeModule;

public class GlowshroomBlock extends MushroomBlock implements IQuarkBlock {

	private final Module module;
	private BooleanSupplier enabledSupplier = () -> true;

	public GlowshroomBlock(Module module) {
		super(Block.Properties.from(Blocks.RED_MUSHROOM).lightValue(14).tickRandomly());
		
		this.module = module;
		RegistryHelper.registerBlock(this, "glowshroom");
		RegistryHelper.setCreativeTab(this, ItemGroup.DECORATIONS);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
		return worldIn.getBlockState(blockpos).getBlock() == GlowshroomUndergroundBiomeModule.glowcelium;
	}
	
	@Override
	public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull IBlockReader world, BlockPos pos, @Nonnull Direction facing, IPlantable plantable) {
		return state.getBlock() == GlowshroomUndergroundBiomeModule.glowcelium;
	}
	
	@Override
	public void tick(@Nonnull BlockState state, @Nonnull ServerWorld worldIn, @Nonnull BlockPos pos, Random rand) {
		if(rand.nextInt(GlowshroomUndergroundBiomeModule.glowshroomGrowthRate) == 0) {
			int i = 5;

			for(BlockPos targetPos : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
				if(worldIn.getBlockState(targetPos).getBlock() == this) {
					--i;

					if(i <= 0)
						return;
				}
			}

			BlockPos shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

			for(int k = 0; k < 4; ++k) {
				if (worldIn.isAirBlock(shiftedPos) && state.isValidPosition(worldIn, shiftedPos))
					pos = shiftedPos;

				shiftedPos = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
			}

			if(worldIn.isAirBlock(shiftedPos) && state.isValidPosition(worldIn, shiftedPos))
				worldIn.setBlockState(shiftedPos, getDefaultState(), 2);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		super.animateTick(stateIn, worldIn, pos, rand);

		if(rand.nextInt(20) == 0)
			worldIn.addParticle(ParticleTypes.END_ROD, pos.getX() + 0.2 + rand.nextFloat() * 0.6, pos.getY() + 0.3F, pos.getZ() + 0.2 + rand.nextFloat() * 0.6, 0, 0, 0);
	}

	@Override
	public boolean canGrow(@Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean isClient) {
		return GlowshroomUndergroundBiomeModule.enableHugeGlowshrooms;
	}

	@Override
	public boolean canUseBonemeal(@Nonnull World worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		return GlowshroomUndergroundBiomeModule.enableHugeGlowshrooms && rand.nextFloat() < 0.4;
	}

	@Override
	public void grow(@Nonnull ServerWorld worldIn, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull BlockState state) {
		if(GlowshroomUndergroundBiomeModule.enableHugeGlowshrooms) {
			worldIn.removeBlock(pos, false);
			if(!HugeGlowshroomBlock.place(worldIn, rand, pos))
				worldIn.setBlockState(pos, getDefaultState());
		}
	}

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}


	@Override
	public GlowshroomBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public Module getModule() {
		return module;
	}
}
