/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/04/2016, 17:44:30 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.handler.DropoffHandler;

public class MessageRestock extends NetworkMessage {

	public boolean filtered;
	
	public MessageRestock() { }
	
	public MessageRestock(boolean filtered) { 
		this.filtered = filtered;
	}

	@Override
	public IMessage handleMessage(MessageContext context) {
		context.getServerHandler().player.getServer().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				DropoffHandler.restock(context.getServerHandler().player, filtered);
			}
		});
		return null;
	}

}
