package vazkii.quark.world.world.underground;

import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenShrub;
import vazkii.quark.base.module.ModuleLoader;

public class UndergroundBiomeLush extends BasicUndergroundBiome {

	WorldGenShrub shrubGen = new WorldGenShrub(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState());

	int grassChance, shrubChance, vineChance;

	public UndergroundBiomeLush() {
		super(Blocks.GRASS.getDefaultState(), null, null);
	}

	@Override
	public void finalFloorPass(World world, BlockPos pos) {
		if(grassChance > 0 && world.rand.nextInt(grassChance) == 0)
			ItemDye.applyBonemeal(new ItemStack(Items.DYE, 1, 14), world, pos);

		if(shrubChance > 0 && world.rand.nextInt(shrubChance) == 0)
			shrubGen.generate(world, world.rand, pos.up());
	}

	@Override
	public void finalWallPass(World world, BlockPos pos) {
		for(EnumFacing facing : EnumFacing.HORIZONTALS) {
			BlockPos off = pos.offset(facing);
			BlockPos up = off.up();
			if(vineChance > 0 && isCeiling(world, up, world.getBlockState(up)) && world.rand.nextInt(vineChance) == 0) {
				IBlockState stateAt = world.getBlockState(off); 
				boolean did = false;
				while(stateAt.getBlock().isAir(stateAt, world, off) && off.getY() > 0) {
					world.setBlockState(off, Blocks.VINE.getDefaultState().withProperty(BlockVine.getPropertyFor(facing.getOpposite()), true), 2);
					off = off.down();
					stateAt = world.getBlockState(off);
					did = true;
				}

				if(did)
					return;
			}
		}
	}

	@Override
	public void setupConfig(String category) {
		grassChance = ModuleLoader.config.getInt("Grass Chance", category, 20, 0, Integer.MAX_VALUE, "The higher, the less grass will spawn");
		shrubChance = ModuleLoader.config.getInt("Shrub Chance", category, 100, 0, Integer.MAX_VALUE, "The higher, the less shrubs will spawn");
		vineChance = ModuleLoader.config.getInt("Vine Chance", category, 8, 0, Integer.MAX_VALUE, "The higher, the less vines will spawn");
	}

}
