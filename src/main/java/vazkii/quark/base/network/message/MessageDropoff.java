/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [28/03/2016, 16:32:34 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.handler.DropoffHandler;

public class MessageDropoff extends NetworkMessage {

	public boolean smart;
	public boolean useContainer;

	public MessageDropoff() { }

	public MessageDropoff(boolean smart, boolean useContainer) {
		this.smart = smart;
		this.useContainer = useContainer;
	}

	@Override
	public IMessage handleMessage(MessageContext context) {
		context.getServerHandler().player.getServer().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				DropoffHandler.dropoff(context.getServerHandler().player, smart, useContainer);
			}
		});
		return null;
	}

}
