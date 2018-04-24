/**
 * This class was created by <Palaster>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [04/23/2018, 12:17:00 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.Iterator;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.ai.EntityAIOpenDoubleDoor;

public class VillagerOpenDoubleDoors extends Feature {

	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event) {
		if(event.getEntity() instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager) event.getEntity();
			for(Iterator<EntityAITaskEntry> it = villager.tasks.taskEntries.iterator(); it.hasNext();) {
				EntityAIBase te = it.next().action;
				if(te instanceof EntityAIOpenDoubleDoor)
					return;
				else if(te instanceof EntityAIOpenDoor)
					it.remove();
			}

			villager.tasks.addTask(4, new EntityAIOpenDoubleDoor(villager, true));
		}
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
}