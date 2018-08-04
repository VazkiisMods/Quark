/**
 * This class was created by <Palaster>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [01/11/2016, 17:26:00 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.ai.EntityAIOpenDoubleDoor;

public class DoubleDoors extends Feature {

	boolean allowVillagers = true;
	
	@Override
	public void setupConfig() {
		allowVillagers = loadPropBool("Allow Villagers to use Double Doors", "", allowVillagers);
	}
	
	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event) {
		if(event.getEntity() instanceof EntityVillager && allowVillagers) {
			EntityVillager villager = (EntityVillager) event.getEntity();
			for(Iterator<EntityAITaskEntry> it = villager.tasks.taskEntries.iterator(); it.hasNext();) {
				EntityAIBase te = it.next().action;
				if(te instanceof EntityAIOpenDoubleDoor)
					return;
				else if(te instanceof EntityAIOpenDoor) {
					it.remove();
					villager.tasks.addTask(4, new EntityAIOpenDoubleDoor(villager, true));
					return;
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
		if(event.getEntityPlayer().isSneaking() || event.isCanceled() || event.getResult() == Result.DENY)
			return;

		World world = event.getWorld();
		IBlockState state = world.getBlockState(event.getPos()).getActualState(world, event.getPos());
		Block block = state.getBlock();

		if(!(block instanceof BlockDoor))
			return;

		EnumFacing direction = state.getValue(BlockDoor.FACING);
		boolean isOpen = state.getValue(BlockDoor.OPEN);
		BlockDoor.EnumHingePosition isMirrored = state.getValue(BlockDoor.HINGE);

		BlockPos mirrorPos = event.getPos().offset(isMirrored == BlockDoor.EnumHingePosition.RIGHT ? direction.rotateYCCW() : direction.rotateY());
		BlockPos doorPos = state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER ? mirrorPos : mirrorPos.down();
		IBlockState other = world.getBlockState(doorPos).getActualState(world, doorPos);

		if(block.getMaterial(state) != Material.IRON && other.getBlock() == (BlockDoor) block && other.getValue(BlockDoor.FACING) == direction && other.getValue(BlockDoor.OPEN) == isOpen && other.getValue(BlockDoor.HINGE) != isMirrored) {

			IBlockState newState = other.cycleProperty(BlockDoor.OPEN);
			world.setBlockState(doorPos, newState, 10);
		}
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "malisisdoors" };
	}

}
