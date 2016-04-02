/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [28/03/2016, 16:32:34 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.quark.base.handler.DropoffHandler;
import vazkii.quark.base.network.Message;

public class MessageDropoff extends Message {

	public boolean smart;
	public boolean useContainer;
	
	public MessageDropoff() { }
	
	public MessageDropoff(boolean smart, boolean useContainer) {
		this.smart = smart;
		this.useContainer = useContainer;
	}
	
	@Override
	public IMessage handleMessage(MessageContext context) {
		DropoffHandler.dropoff(context.getServerHandler().playerEntity, smart, useContainer);
		return null;
	}
	
}
