package vazkii.quark.automation.feature;

import net.minecraft.block.*;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.util.ItemTickHandler.EntityItemTickEvent;
import vazkii.quark.base.module.Feature;

public class PistonsPushPullItems extends Feature {
	
	double force = 0.48F;
	
	@Override
	public void setupConfig() {
		force = loadPropDouble("Push Strength", "", force);
	}
	
	@SubscribeEvent
	public void onEntityTick(EntityItemTickEvent event) {
		EntityItem entity = event.getEntityItem();
		World world = entity.getEntityWorld();
		BlockPos pos = entity.getPosition();
		for(EnumFacing face : EnumFacing.VALUES) {
			BlockPos offsetPos1 = pos.offset(face);
			if(world.isBlockLoaded(offsetPos1)) {
				IBlockState state = world.getBlockState(offsetPos1);
				if(state.getBlock() == Blocks.PISTON_EXTENSION) {
					//check for adjacent moving sticky blocks e.g. slime blocks or other modded sticky blocks
					TileEntity tile = world.getTileEntity(offsetPos1);
					if(tile instanceof TileEntityPiston) {
						TileEntityPiston movingBlockTile = (TileEntityPiston) tile;
						
						IBlockState adjacentMovingState = movingBlockTile.getPistonState();
						if(adjacentMovingState.getBlock().isStickyBlock(adjacentMovingState)) {
							//nudge the item in the same direction as the sticky block
							EnumFacing nudgeDirection = state.getValue(BlockDirectional.FACING);
							if(!movingBlockTile.isExtending()) nudgeDirection = nudgeDirection.getOpposite();
							//give items a weaker push, to simulate sticking to slime
							nudgeItem(world, entity, nudgeDirection, false, 0.25f);
						}
					}
					
					//check for moving blocks or normal piston heads pushing *in* to this item
					if(state.getValue(BlockDirectional.FACING) == face.getOpposite() && state.getValue(BlockPistonExtension.TYPE) == EnumPistonType.DEFAULT) {
						nudgeItem(world, entity, face.getOpposite(), true);
					}
				}
			}
			
			BlockPos offsetPos2 = pos.offset(face, 2);
			if(world.isBlockLoaded(offsetPos2)) {
				IBlockState state = world.getBlockState(offsetPos2);
				if(state.getBlock() == Blocks.PISTON_EXTENSION) {
					
					//check for adjacent moving sticky piston heads pulling *away* from this item
					if(state.getValue(BlockDirectional.FACING) == face.getOpposite() && state.getValue(BlockPistonExtension.TYPE) == EnumPistonType.STICKY) {
						//only moving sticky piston *heads* should affect items in this way
						TileEntity tile = world.getTileEntity(offsetPos2);
						if(tile instanceof TileEntityPiston) {
							TileEntityPiston movingBlockTile = (TileEntityPiston) tile;
							if(movingBlockTile.getPistonState().getBlock() == Blocks.STICKY_PISTON) {
								nudgeItem(world, entity, face, false);
							}
						}
					}
				}
			}
		}
	}
	
	private void nudgeItem(World world, EntityItem entity, EnumFacing whichWay, boolean showParticles) {
		nudgeItem(world, entity, whichWay, showParticles, (float) force);
	}
	
	private void nudgeItem(World world, EntityItem entity, EnumFacing whichWay, boolean showParticles, float forceOverride) {
		float x = forceOverride * whichWay.getFrontOffsetX();
		float y = forceOverride * whichWay.getFrontOffsetY();
		float z = forceOverride * whichWay.getFrontOffsetZ();
		float px = x == 0 ? 0.4F : 0;
		float py = y == 0 ? 0.4F : 0;
		float pz = z == 0 ? 0.4F : 0;
		entity.addVelocity(x, y, z);
		if(showParticles && world instanceof WorldServer)
			((WorldServer) world).spawnParticle(EnumParticleTypes.CRIT, entity.posX, entity.posY, entity.posZ, 12, px, py, pz, 0);
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
