package org.violetmoon.zetaimplforge.client.event.player;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.violetmoon.zeta.client.event.play.ZRenderPlayer;

public abstract class ForgeZRenderPlayer implements ZRenderPlayer {
	private final RenderPlayerEvent e;

	public ForgeZRenderPlayer(RenderPlayerEvent e) {
		this.e = e;
	}

	@Override
	public PlayerRenderer getRenderer() {return e.getRenderer();}

	@Override
	public float getPartialTick() {return e.getPartialTick();}

	@Override
	public PoseStack getPoseStack() {return e.getPoseStack();}

	@Override
	public MultiBufferSource getMultiBufferSource() {return e.getMultiBufferSource();}

	@Override
	public int getPackedLight() {return e.getPackedLight();}

	@Override
	public Player getEntity() {return e.getEntity();}

	public static class Pre extends ForgeZRenderPlayer implements ZRenderPlayer.Pre {
		public Pre(RenderPlayerEvent.Pre e) {
			super(e);
		}
	}

	public static class Post extends ForgeZRenderPlayer implements ZRenderPlayer.Post {
		public Post(RenderPlayerEvent.Post e) {
			super(e);
		}
	}
}
