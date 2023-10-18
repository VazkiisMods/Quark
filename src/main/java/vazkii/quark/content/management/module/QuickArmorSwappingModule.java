package vazkii.quark.content.management.module;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = "management", hasSubscriptions = true)
public class QuickArmorSwappingModule extends QuarkModule {

	@Config public static boolean swapOffHand = true;

	@SubscribeEvent
	public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
		Player player = event.getEntity();

		if(player.isSpectator() || player.getAbilities().instabuild || !(event.getTarget() instanceof ArmorStand armorStand))
			return;

		if(player.isCrouching()) {
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);

			swapSlot(player, armorStand, EquipmentSlot.HEAD);
			swapSlot(player, armorStand, EquipmentSlot.CHEST);
			swapSlot(player, armorStand, EquipmentSlot.LEGS);
			swapSlot(player, armorStand, EquipmentSlot.FEET);
			if(swapOffHand)
				swapSlot(player, armorStand, EquipmentSlot.OFFHAND);
		}
	}

	private void swapSlot(Player player, ArmorStand armorStand, EquipmentSlot slot) {
		ItemStack playerItem = player.getItemBySlot(slot);
		ItemStack armorStandItem = armorStand.getItemBySlot(slot);

		if(EnchantmentHelper.hasBindingCurse(playerItem))
			return; // lol no

		ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);

		if(armorStandItem.isEmpty() && !held.isEmpty() && Player.getEquipmentSlotForItem(held) == slot) {
			ItemStack copy = held.copy();
			player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
			armorStandItem = copy;
		}

		player.setItemSlot(slot, armorStandItem);
		armorStand.setItemSlot(slot, playerItem);
	}

}
