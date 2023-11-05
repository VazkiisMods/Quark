package vazkii.zetaimplforge.client.event;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import vazkii.zeta.client.event.ZRenderOverlay;

public class ForgeZRenderOverlay implements ZRenderOverlay {
	private final RenderGuiOverlayEvent e;

	public ForgeZRenderOverlay(RenderGuiOverlayEvent e) {
		this.e = e;
	}

	@Override
	public Window getWindow() {
		return e.getWindow();
	}

	@Override
	public PoseStack getPoseStack() {
		return e.getPoseStack();
	}

	@Override
	public float getPartialTick() {
		return e.getPartialTick();
	}

	@Override
	public boolean shouldDrawSurvivalElements() {
		return Minecraft.getInstance().gui instanceof ForgeGui fgui && fgui.shouldDrawSurvivalElements();
	}

	@Override
	public int getLeftHeight() {
		return Minecraft.getInstance().gui instanceof ForgeGui fgui ? fgui.leftHeight : 39;
	}

	public static class ArmorLevel extends ForgeZRenderOverlay implements ZRenderOverlay.ArmorLevel {
		public ArmorLevel(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderOverlay.ArmorLevel implements ZRenderOverlay.ArmorLevel.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderOverlay.ArmorLevel implements ZRenderOverlay.ArmorLevel.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class Chat extends ForgeZRenderOverlay implements ZRenderOverlay.Chat {
		public Chat(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderOverlay.Chat implements ZRenderOverlay.Chat.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderOverlay.Chat implements ZRenderOverlay.Chat.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class Crosshair extends ForgeZRenderOverlay implements ZRenderOverlay.Crosshair {
		public Crosshair(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderOverlay.Crosshair implements ZRenderOverlay.Crosshair.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderOverlay.Crosshair implements ZRenderOverlay.Crosshair.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}

	public static class Hotbar extends ForgeZRenderOverlay implements ZRenderOverlay.Hotbar {
		public Hotbar(RenderGuiOverlayEvent e) {
			super(e);
		}

		public static class Pre extends ForgeZRenderOverlay.Hotbar implements ZRenderOverlay.Hotbar.Pre {
			public Pre(RenderGuiOverlayEvent.Pre e) {
				super(e);
			}
		}

		public static class Post extends ForgeZRenderOverlay.Hotbar implements ZRenderOverlay.Hotbar.Post {
			public Post(RenderGuiOverlayEvent.Post e) {
				super(e);
			}
		}
	}
}
