package vazkii.quark.base.network.message;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.entity.EntityChestPassenger;

public class MessageRequestPassengerChest extends NetworkMessage<MessageRequestPassengerChest> {

	@Override
	public IMessage handleMessage(MessageContext context) {
		EntityPlayer player = context.getServerHandler().player;

		if(player.isRiding() && player.openContainer == player.inventoryContainer) {
			Entity riding = player.getRidingEntity();
			if(riding instanceof EntityBoat) {
				List<Entity> passengers = riding.getPassengers();
				for(Entity passenger : passengers)
					if(passenger instanceof EntityChestPassenger)
						player.displayGUIChest((EntityChestPassenger) passenger);
			}
		}
		
		return null;
	}

}
