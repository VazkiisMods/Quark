package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.handler.SortingHandler;

public class MessageSortInventory extends NetworkMessage {

	public boolean forcePlayer;
	
	public MessageSortInventory() { }

	public MessageSortInventory(boolean forcePlayer) { 
		this.forcePlayer = forcePlayer;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		SortingHandler.sortInventory(context.getServerHandler().playerEntity, forcePlayer);
		return null;
	}

}
