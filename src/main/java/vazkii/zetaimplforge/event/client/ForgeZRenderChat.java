package vazkii.zetaimplforge.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.client.ZRenderChat;

@FiredAs(ZRenderChat.class)
public abstract class ForgeZRenderChat implements ZRenderChat {
	protected final RenderGuiOverlayEvent e;

	public ForgeZRenderChat(RenderGuiOverlayEvent e) {
		this.e = e;
	}

	@Override
	public PoseStack getPoseStack() {
		return e.getPoseStack();
	}

	@FiredAs(ZRenderChat.Pre.class)
	public static class Pre extends ForgeZRenderChat implements ZRenderChat.Pre {
		public Pre(RenderGuiOverlayEvent e) {
			super(e);
		}

		@Override
		public boolean isCanceled() {
			return e.isCanceled();
		}

		@Override
		public void setCanceled(boolean cancel) {
			e.setCanceled(cancel);
		}
	}

	@FiredAs(ZRenderChat.Post.class)
	public static class Post extends ForgeZRenderChat implements ZRenderChat.Post {
		public Post(RenderGuiOverlayEvent e) {
			super(e);
		}
	}
}
