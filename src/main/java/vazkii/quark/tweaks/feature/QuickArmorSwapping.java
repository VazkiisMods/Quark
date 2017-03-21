/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [27/03/2016, 21:55:50 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class QuickArmorSwapping extends Feature {

	boolean offhandSwapping;

	@Override
	public void setupConfig() {
		offhandSwapping = loadPropBool("Swap off-hand with armor", "", true);
	}

	@SubscribeEvent
	public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
		EntityPlayer player = event.getEntityPlayer();

		if(event.getTarget().world.isRemote || player.isSpectator() || player.isCreative() || !(event.getTarget() instanceof EntityArmorStand))
			return;

		if(player.isSneaking()) {
			event.setCanceled(true);
			EntityArmorStand armorStand = (EntityArmorStand) event.getTarget();

			swapSlot(player, armorStand, EntityEquipmentSlot.HEAD);
			swapSlot(player, armorStand, EntityEquipmentSlot.CHEST);
			swapSlot(player, armorStand, EntityEquipmentSlot.LEGS);
			swapSlot(player, armorStand, EntityEquipmentSlot.FEET);
			if(offhandSwapping)
				swapSlot(player, armorStand, EntityEquipmentSlot.OFFHAND);
		}
	}

	private void swapSlot(EntityPlayer player, EntityArmorStand armorStand, EntityEquipmentSlot slot) {
		ItemStack playerItem = player.getItemStackFromSlot(slot);
		ItemStack armorStandItem = armorStand.getItemStackFromSlot(slot);
		player.setItemStackToSlot(slot, armorStandItem);
		armorStand.setItemStackToSlot(slot, playerItem);
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "iberia" };
	}
}
