package vazkii.quark.world.world;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.world.feature.NetherSmoker;

public class SmokerGenerator implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.isNether() && random.nextFloat() < NetherSmoker.chunkChance)
			for(int i = 0; i < NetherSmoker.triesPerChunk; i++) {
				int x = chunkX * 16 + random.nextInt(16) + 8;
				int z = chunkZ * 16 + random.nextInt(16) + 8;

				BlockPos pos = new BlockPos(x, 50, z);
				
				while(pos.getY() > 10) {
					IBlockState state = world.getBlockState(pos);
					if(state.getBlock() == Blocks.LAVA) {
						if(world.getBlockState(pos.down()).getBlock() == Blocks.NETHERRACK)
							for(EnumFacing face : EnumFacing.HORIZONTALS)
								if(world.getBlockState(pos.offset(face)).getBlock() == Blocks.NETHERRACK) {
									world.setBlockState(pos.down(), NetherSmoker.smoker.getDefaultState());
									break;
								}
						break;
					}
					
					pos = pos.down();
				}
			}
	}

}
