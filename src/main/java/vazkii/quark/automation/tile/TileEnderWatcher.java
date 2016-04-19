/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [18/04/2016, 21:51:27 (GMT)]
 */
package vazkii.quark.automation.tile;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.automation.block.BlockEnderWatcher;
import vazkii.quark.base.block.tile.TileMod;

public class TileEnderWatcher extends TileMod implements ITickable {

	@Override
	public void update() {
		if(worldObj.isRemote)
			return;

		boolean wasLooking = worldObj.getBlockState(getPos()).getValue(BlockEnderWatcher.WATCHED);
		int range = 80;
		List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range)));

		boolean looking = false;
		for(EntityPlayer player : players) {
			ItemStack helm = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			if(helm != null && helm.getItem() == Item.getItemFromBlock(Blocks.pumpkin))
				continue;

			RayTraceResult pos = raytraceFromEntity(worldObj, player, true, 64);
			if(pos != null && pos.getBlockPos() != null && pos.getBlockPos().equals(getPos())) {
				looking = true;
				break;
			}
		}

		if(looking != wasLooking && !worldObj.isRemote)
			worldObj.setBlockState(getPos(), worldObj.getBlockState(getPos()).withProperty(BlockEnderWatcher.WATCHED, looking), 1 | 2);
		
		if(looking) {
			double x = getPos().getX() - 0.1 + Math.random() * 1.2;
			double y = getPos().getY() - 0.1 + Math.random() * 1.2;
			double z = getPos().getZ() - 0.1 + Math.random() * 1.2;

			((WorldServer) worldObj).spawnParticle(EnumParticleTypes.REDSTONE, false, x, y, z, 0, 1.0D, 0.0D, 0.0D, 1.0D);
		}
	}
	
	public static RayTraceResult raytraceFromEntity(World world, Entity player, boolean par3, double range) {
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * f;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * f;
		if(player instanceof EntityPlayer)
			d1 += ((EntityPlayer) player).eyeHeight;
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
		Vec3d vec3 = new Vec3d(d0, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		double d3 = range;
		Vec3d vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
		return world.rayTraceBlocks(vec3, vec31, par3);
	}
	
}
