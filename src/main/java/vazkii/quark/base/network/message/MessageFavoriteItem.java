/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [02/04/2016, 18:01:17 (GMT)]
 */
package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.quark.base.network.Message;
import vazkii.quark.management.feature.FavoriteItems;

public class MessageFavoriteItem extends Message {

	public int index;

	public MessageFavoriteItem() { }

	public MessageFavoriteItem(int index) {
		this.index = index;
	}

	@Override
	public IMessage handleMessage(MessageContext context) {
		FavoriteItems.favoriteItem(context.getServerHandler().playerEntity, index);
		return null;
	}

}
