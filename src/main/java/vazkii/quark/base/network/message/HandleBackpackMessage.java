package vazkii.quark.base.network.message;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.arl.network.IMessage;
import vazkii.quark.oddities.container.BackpackContainer;

public class HandleBackpackMessage implements IMessage {

	private static final long serialVersionUID = 3474816381329541425L;

	public boolean open;

	public HandleBackpackMessage() { }

	public HandleBackpackMessage(boolean open) { 
		this.open = open;
	}

	@Override
	public boolean receive(Context context) {
		ServerPlayerEntity player = context.getSender();
		context.enqueueWork(() -> {
			if(open) {
				ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
				if(stack.getItem() instanceof INamedContainerProvider)
					NetworkHooks.openGui(player, (INamedContainerProvider) stack.getItem(), player.getPosition());
			} else {
				BackpackContainer.saveCraftingInventory(player);
				player.openContainer = player.container;
			}
		});

		return true;
	}

}
