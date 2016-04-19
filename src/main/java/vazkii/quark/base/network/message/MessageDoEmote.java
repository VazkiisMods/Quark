/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 22:10:42 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.quark.base.Quark;
import vazkii.quark.base.network.Message;

public class MessageDoEmote extends Message {

	public String emoteName;
	public String playerName;

	public MessageDoEmote() { }

	public MessageDoEmote(String emoteName, String playerName) {
		this.emoteName = emoteName;
		this.playerName = playerName;
	}

	@Override
	public IMessage handleMessage(MessageContext context) {
		Quark.proxy.doEmote(playerName, emoteName);
		return null;
	}

}
