/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [05/04/2016, 22:55:30 (GMT)]
 */
package vazkii.quark.vanity.feature;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import vazkii.quark.base.Quark;
import vazkii.quark.base.lib.LibEntityIDs;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;

public class SitInStairs extends Feature {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		String name = LibMisc.PREFIX_MOD + "seat";
		EntityRegistry.registerModEntity(new ResourceLocation(name), Seat.class, name, LibEntityIDs.SEAT, Quark.instance, 16, 128, false);
	}
	
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		if(player.getRidingEntity() != null)
			return;
		
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		
		Vec3d vec = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		double maxDist = 2;
		if((vec.x - player.posX) * (vec.x - player.posX) + (vec.y - player.posY) * (vec.y - player.posY) + (vec.z - player.posZ) * (vec.z - player.posZ) > maxDist * maxDist)
			return;
		
		IBlockState state = world.getBlockState(pos);

		ItemStack stack1 = player.getHeldItemMainhand();
		ItemStack stack2 = player.getHeldItemOffhand();
		if(!stack1.isEmpty() || !stack2.isEmpty())
			return;

		if(state.getBlock() instanceof BlockStairs && state.getValue(BlockStairs.HALF) == EnumHalf.BOTTOM && !state.getBlock().isSideSolid(state, world, pos, event.getFace()) && canBeAbove(world, pos)) {
			List<Seat> seats = world.getEntitiesWithinAABB(Seat.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));

			if(seats.isEmpty()) {
				Seat seat = new Seat(world, pos);
				world.spawnEntity(seat);
				event.getEntityPlayer().startRiding(seat);
			}
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	public static boolean canBeAbove(World world, BlockPos pos) {
		BlockPos upPos = pos.up();
		IBlockState state = world.getBlockState(upPos);
		Block block = state.getBlock();
		return block.getCollisionBoundingBox(state, world, upPos) == null;
	}

	public static class Seat extends Entity {

		public Seat(World world, BlockPos pos) {
			this(world);

			setPosition(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
		}

		public Seat(World par1World) {
			super(par1World);

			setSize(0F, 0F);
		}

		@Override
		public void onUpdate() {
			super.onUpdate();

			BlockPos pos = getPosition();
			if(pos != null && !(getEntityWorld().getBlockState(pos).getBlock() instanceof BlockStairs) || !canBeAbove(getEntityWorld(), pos)) {
				setDead();
				return;
			}

			List<Entity> passangers = getPassengers();
			if(passangers.isEmpty())
				setDead();
			for(Entity e : passangers)
				if(e.isSneaking())
					setDead();
		}

		@Override protected void entityInit() { }
		@Override protected void readEntityFromNBT(NBTTagCompound nbttagcompound) { }
		@Override protected void writeEntityToNBT(NBTTagCompound nbttagcompound) { }
	}

}
