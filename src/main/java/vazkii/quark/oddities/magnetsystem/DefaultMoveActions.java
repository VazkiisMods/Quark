package vazkii.quark.oddities.magnetsystem;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;

public class DefaultMoveActions {

	public static void addActions(HashMap<Block, IMagnetMoveAction> map) {
		map.put(Blocks.STONECUTTER, DefaultMoveActions::stonecutterMoved);
		map.put(Blocks.HOPPER, DefaultMoveActions::hopperMoved);
	}
	
	private static void stonecutterMoved(World world, BlockPos pos, Direction direction, BlockState state, TileEntity tile) {
		if(!world.isRemote) {
			BlockPos up = pos.up();
			BlockState breakState = world.getBlockState(up);
			double hardness = breakState.getBlockHardness(world, up); 
			if(hardness > -1 && hardness < 3)
				world.destroyBlock(up, true);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static void hopperMoved(World world, BlockPos pos, Direction direction, BlockState state, TileEntity tile) {
		if(!world.isRemote && tile instanceof HopperTileEntity) {
			HopperTileEntity hopper = (HopperTileEntity) tile;
			hopper.setTransferCooldown(0);
			
			Direction dir = state.get(HopperBlock.FACING);
			BlockPos offPos = pos.offset(dir);
			BlockPos targetPos = pos.offset(direction);
			if(offPos.equals(targetPos))
				return;
			
			if(world.isAirBlock(offPos))
				for(int i = 0; i < hopper.getSizeInventory(); i++) {
					ItemStack stack = hopper.getStackInSlot(i);
					if(!stack.isEmpty()) {
						ItemStack drop = stack.copy();
						drop.setCount(1);
						hopper.decrStackSize(i, 1);
						
						boolean shouldDrop = true;
						if(drop.getItem() instanceof BlockItem) {
							BlockPos farmlandPos = offPos.down();
							if(world.isAirBlock(farmlandPos))
								farmlandPos = farmlandPos.down();
							
							if(world.getBlockState(farmlandPos).getBlock() == Blocks.FARMLAND) {
								Block seedType = ((BlockItem) drop.getItem()).getBlock();
								if(seedType instanceof IPlantable) {
									BlockPos seedPos = farmlandPos.up();
									if(seedType.isValidPosition(state, world, seedPos)) {
										BlockState seedState = seedType.getDefaultState();
										((ServerWorld) world).playSound(null, seedPos, seedType.getSoundType(seedState).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
										
										world.setBlockState(seedPos, seedState);
										shouldDrop = false;
									}
								}
							}
						}
						
						if(shouldDrop) {
							double x = pos.getX() + 0.5 + ((double) dir.getXOffset() * 0.7);
							double y = pos.getY() + 0.15 + ((double) dir.getYOffset() * 0.4);
							double z = pos.getZ() + 0.5 + ((double) dir.getZOffset() * 0.7);
							ItemEntity entity = new ItemEntity(world, x, y, z, drop);
							entity.setMotion(Vec3d.ZERO);
							world.addEntity(entity);
						}

						return;
					}
				}
		}
	}
	
}
