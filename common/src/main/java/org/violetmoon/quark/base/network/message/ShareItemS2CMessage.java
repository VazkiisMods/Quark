package org.violetmoon.quark.base.network.message;

import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import org.violetmoon.quark.content.management.module.ItemSharingModule;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.util.UUID;

public class ShareItemS2CMessage implements IZetaMessage {
	public UUID senderUuid;
	public Component senderName;
	public ItemStack stack;

	public ShareItemS2CMessage() {

	}

	public ShareItemS2CMessage(UUID senderUuid, Component senderName, ItemStack stack) {
		this.senderUuid = senderUuid;
		this.senderName = senderName;
		this.stack = stack;
	}

	@Override
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> {
			if(Minecraft.getInstance().isBlocked(senderUuid))
				return;

			Minecraft.getInstance().gui.getChat().addMessage(
					Component.translatable("chat.type.text", senderName, ItemSharingModule.createStackComponent(stack)),
					null,
					new GuiMessageTag(0xDEB483, null, null, "Quark shared item")
			);

		});

		return true;
	}
}
