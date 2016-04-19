/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [02/04/2016, 17:44:30 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.quark.base.handler.DropoffHandler;
import vazkii.quark.base.network.Message;

public class MessageRestock extends Message {

	public MessageRestock() { }

	@Override
	public IMessage handleMessage(MessageContext context) {
		DropoffHandler.restock(context.getServerHandler().playerEntity);
		return null;
	}

}
