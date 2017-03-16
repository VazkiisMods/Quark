package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;

public class MessageChangeHotbar extends NetworkMessage<MessageChangeHotbar> {

	public int bar;
	
	public MessageChangeHotbar() { }
	
	public MessageChangeHotbar(int bar) {
		this.bar = bar;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		EntityPlayer player = context.getServerHandler().playerEntity;
		
		if(bar > 0 && bar <= 3)
			for(int i = 0; i < 9; i++)
				swap(player.inventory, i, i + bar * 9);
		
		return null;
	}
	
	public void swap(IInventory inv, int slot1, int slot2) {
		ItemStack stack1 = inv.getStackInSlot(slot1);
		ItemStack stack2 = inv.getStackInSlot(slot2);
		inv.setInventorySlotContents(slot2, stack1);
		inv.setInventorySlotContents(slot1, stack2);
	}
	
}
