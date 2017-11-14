package vazkii.quark.world.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import vazkii.arl.block.tile.TileMod;

public class TileSmoker extends TileMod implements ITickable {

	@Override
	public void update() {
		if(!world.isRemote || isFullBlock(pos.up()))
			return;
		
		BlockPos source = findChimneyEnd();
		int yDiff = source.getY() - pos.getY();
    	for(int i = 0; i < yDiff - 1; i++) {
    		float x = pos.getX() + 0.2F + world.rand.nextFloat() * 0.6F;
        	float y = pos.getY() + world.rand.nextFloat() * (yDiff - 1) - 0.4F;
        	float z = pos.getZ() + 0.2F + world.rand.nextFloat() * 0.6F;
        	
        	world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), x, y, z, 0, 0, 0);
    	}
    	
    	for(int i = 0; i < 6; i++) {
    		float x = source.getX() + 0.2F + world.rand.nextFloat() * 0.6F;
        	float y = source.getY() + world.rand.nextFloat() - 0.4F;
        	float z = source.getZ() + 0.2F + world.rand.nextFloat() * 0.6F;
        	
        	world.spawnAlwaysVisibleParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), x, y, z, 0, 0, 0);
    	}
	}
	
	private boolean isFullBlock(BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		AxisAlignedBB aabb = state.getCollisionBoundingBox(world, pos);
		return Block.FULL_BLOCK_AABB.equals(aabb);
	}

	private BlockPos findChimneyEnd() {
		BlockPos currPos = pos;
		
		while(currPos.getY() < 255) {
			BlockPos nextPos = currPos.up();
			if(isFullBlock(nextPos))
				return currPos;
			
			for(EnumFacing face : EnumFacing.HORIZONTALS) {
				BlockPos checkPos = nextPos.offset(face);
				if(!isFullBlock(checkPos))
					return nextPos;
			}
			
			currPos = nextPos;
		}
		
		return pos;
	}
	
}
