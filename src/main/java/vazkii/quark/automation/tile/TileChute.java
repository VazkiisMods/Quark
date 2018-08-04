package vazkii.quark.automation.tile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.automation.block.BlockChute;

public class TileChute extends TileSimpleInventory {

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if(!itemstack.isEmpty()) {
			EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, itemstack);
			entity.motionX = entity.motionY = entity.motionZ = 0;
			world.spawnEntity(entity);
		}
	}
	
	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public boolean isAutomationEnabled() {
		if(world.getBlockState(pos).getValue(BlockChute.ENABLED)) {
			BlockPos below = pos.down();
			IBlockState state = world.getBlockState(below);
			Block block = state.getBlock();
			return block.isAir(state, world, below) || state.getCollisionBoundingBox(world, below) == null;
		}
		return false;
	}
	
}
