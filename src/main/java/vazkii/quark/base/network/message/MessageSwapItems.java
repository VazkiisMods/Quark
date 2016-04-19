/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [31/03/2016, 02:55:00 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.quark.base.network.Message;
import vazkii.quark.management.feature.FToSwitchItems;

public class MessageSwapItems extends Message {

	public int index;

	public MessageSwapItems() { }

	public MessageSwapItems(int index) {
		this.index = index;
	}

	@Override
	public IMessage handleMessage(MessageContext context) {
		FToSwitchItems.switchItems(context.getServerHandler().playerEntity, index);
		return null;
	}

}
