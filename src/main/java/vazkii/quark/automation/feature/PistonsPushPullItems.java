package vazkii.quark.automation.feature;

import net.minecraft.block.*;
import net.minecraft.block.BlockPistonExtension.EnumPistonType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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

	float force = 0.48F;
	
	@Override
	public void setupConfig() {
		force = (float) loadPropDouble("Push Strength", "", force);
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
				//check for moving blocks or normal piston heads pushing *in* to this item
				if(state.getBlock() == Blocks.PISTON_EXTENSION && state.getValue(BlockDirectional.FACING) == face.getOpposite() && state.getValue(BlockPistonExtension.TYPE) == EnumPistonType.DEFAULT)
					nudgeItem(world, entity, face.getOpposite(), true);
			}
			
			boolean closeToEdge = new BlockPos(entity.posX + face.getFrontOffsetX() * .5, entity.posY + face.getFrontOffsetY() * .5, entity.posZ + face.getFrontOffsetZ() * .5).equals(offsetPos1);
			
			if(closeToEdge) {
				BlockPos offsetPos2 = pos.offset(face, 2);
				if(world.isBlockLoaded(offsetPos2)) {
					IBlockState state = world.getBlockState(offsetPos2);
					if(state.getBlock() == Blocks.PISTON_EXTENSION) {
						//check for adjacent moving sticky piston heads pulling *away* from this item
						if(state.getValue(BlockDirectional.FACING) == face.getOpposite() && state.getValue(BlockPistonExtension.TYPE) == EnumPistonType.STICKY) {
							//verify it's a moving head, not just any old block
							TileEntity tile = world.getTileEntity(offsetPos2);
							if(tile instanceof TileEntityPiston) {
								TileEntityPiston movingBlockTile = (TileEntityPiston) tile;
								IBlockState movingBlockState = movingBlockTile.getPistonState();
								Block movingBlock = movingBlockState.getBlock();
								if(movingBlock == Blocks.STICKY_PISTON)
									nudgeItem(world, entity, face, false);
							}
						}
					}
				}
			}
		}
	}
	
	private void nudgeItem(World world, EntityItem entity, EnumFacing whichWay, boolean showParticles) {
		float x = force * whichWay.getFrontOffsetX();
		float y = force * whichWay.getFrontOffsetY();
		float z = force * whichWay.getFrontOffsetZ();
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
