package vazkii.quark.content.management.module;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZPlayerInteract;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "management")
public class QuickArmorSwappingModule extends ZetaModule {

	@Config public static boolean swapOffHand = true;

	@PlayEvent
	public void onEntityInteractSpecific(ZPlayerInteract.EntityInteractSpecific event) {
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
