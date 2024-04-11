package org.violetmoon.quark.base.network.message.oddities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;

import org.violetmoon.quark.addons.oddities.inventory.BackpackMenu;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;

public class HandleBackpackMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = 3474816381329541425L;

	public boolean open;

	public HandleBackpackMessage() {}

	public HandleBackpackMessage(boolean open) {
		this.open = open;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		ServerPlayer player = context.getSender();
		context.enqueueWork(() -> {
			if(open) {
				ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
				if(stack.getItem() instanceof MenuProvider && player.containerMenu != null) {
					ItemStack holding = player.containerMenu.getCarried().copy();
					player.containerMenu.setCarried(ItemStack.EMPTY);
					NetworkHooks.openScreen(player, (MenuProvider) stack.getItem(), player.blockPosition());
					player.containerMenu.setCarried(holding);
				}
			} else {
				if(player.containerMenu != null) {
					ItemStack holding = player.containerMenu.getCarried();
					player.containerMenu.setCarried(ItemStack.EMPTY);

					BackpackMenu.saveCraftingInventory(player);
					player.containerMenu = player.inventoryMenu;
					player.inventoryMenu.setCarried(holding);
				}
			}
		});

		return true;
	}

}
