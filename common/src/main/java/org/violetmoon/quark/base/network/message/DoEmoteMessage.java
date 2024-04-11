package org.violetmoon.quark.base.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.violetmoon.quark.content.tweaks.client.emote.EmoteHandler;
import org.violetmoon.zeta.network.IZetaMessage;
import org.violetmoon.zeta.network.IZetaNetworkEventContext;

import java.io.Serial;
import java.util.UUID;

public class DoEmoteMessage implements IZetaMessage {

	@Serial
	private static final long serialVersionUID = -7952633556330869633L;

	public String emote;
	public UUID playerUUID;
	public int tier;

	public DoEmoteMessage() {}

	public DoEmoteMessage(String emote, UUID playerUUID, int tier) {
		this.emote = emote;
		this.playerUUID = playerUUID;
		this.tier = tier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean receive(IZetaNetworkEventContext context) {
		context.enqueueWork(() -> {
			Level world = Minecraft.getInstance().level;
			Player player = world.getPlayerByUUID(playerUUID);
			EmoteHandler.putEmote(player, emote, tier);
		});

		return true;
	}

}
