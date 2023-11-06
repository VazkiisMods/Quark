package vazkii.quark.content.management.module;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;
import vazkii.quark.base.QuarkClient;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.ChangeHotbarMessage;
import vazkii.zeta.client.event.ZEndClientTick;
import vazkii.zeta.client.event.ZInput;
import vazkii.zeta.client.event.ZKeyMapping;
import vazkii.zeta.client.event.ZRenderGuiOverlay;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "management")
public class HotbarChangerModule extends ZetaModule {
	private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widget.png");

	private static final int ANIMATION_TIME = 10;
	private static final int MAX_HEIGHT = 90;
	private static final int ANIM_PER_TICK = MAX_HEIGHT / ANIMATION_TIME;

	public static int height = 0;
	public static int currentHeldItem = -1;
	public static boolean animating;
	public static boolean keyDown;
	public static boolean hotbarChangeOpen, shifting;

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends HotbarChangerModule {
		private static KeyMapping changeHotbarKey;

		@LoadEvent
		public void registerKeybinds(ZKeyMapping event) {
			changeHotbarKey = event.init("quark.keybind.change_hotbar", "z", QuarkClient.MISC_GROUP);
		}

		@PlayEvent
		public void onMouseInput(ZInput.MouseButton event) {
			acceptInput(-1);
		}

		@PlayEvent
		public void onKeyInput(ZInput.Key event) {
			acceptInput(event.getKey());
		}

		//fixme Needs splitting up
		@PlayEvent
		public void hudHeathPre(ZRenderGuiOverlay.PlayerHealth.Pre event) {
			float shift = -getRealHeight(event.getPartialTick()) + 22;
			if (shift < 0) {
				event.getPoseStack().translate(0, shift, 0);
				shifting = true;
			}
		}

		@PlayEvent
		public void hudDebugTextPre(ZRenderGuiOverlay.DebugText.Pre event) {
			hudOverlay(event);
		}

		@PlayEvent
		public void hudPotionIconsPre(ZRenderGuiOverlay.PotionIcons.Pre event) {
			hudOverlay(event);
		}

		public void hudOverlay(ZRenderGuiOverlay event) {
			float shift = -getRealHeight(event.getPartialTick()) + 22;
			if (shifting) {
				event.getPoseStack().translate(0, -shift, 0);
				shifting = false;
			}
		}

		@PlayEvent
		public void hudPost(ZRenderGuiOverlay.Hotbar.Post event) {
			if(height <= 0)
				return;

			Minecraft mc = Minecraft.getInstance();
			Player player = mc.player;
			PoseStack matrix = event.getPoseStack();

			Window res = event.getWindow();
			float realHeight = getRealHeight(event.getPartialTick());
			float xStart = res.getGuiScaledWidth() / 2f - 91;
			float yStart = res.getGuiScaledHeight() - realHeight;

			ItemRenderer render = mc.getItemRenderer();

			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, WIDGETS);
			for(int i = 0; i < 3; i++) {
				matrix.pushPose();
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.75F);
				matrix.translate(xStart, yStart + i * 21, 0);
				mc.gui.blit(matrix, 0, 0, 0, 0, 182, 22);
				matrix.popPose();
			}

			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

			for(int i = 0; i < 3; i++) {
				String draw = Integer.toString(i + 1);
				KeyMapping key = mc.options.keyHotbarSlots[i];
				if(!key.isUnbound()) {
					draw = key.getTranslatedKeyMessage().getString();
				}

				draw = ChatFormatting.BOLD + draw;

				mc.font.drawShadow(matrix, draw, xStart - mc.font.width(draw) - 2, yStart + i * 21 + 7, 0xFFFFFF);
			}

			for(int i = 0; i < 27; i++) {
				ItemStack invStack = player.getInventory().getItem(i + 9);
				int x = (int) (xStart + (i % 9) * 20 + 3);
				int y = (int) (yStart + (i / 9) * 21 + 3);

				render.renderAndDecorateItem(invStack, x, y);
				render.renderGuiItemDecorations(mc.font, invStack, x, y);
			}
		}

		@PlayEvent
		public void onTick(ZEndClientTick event) {
			Player player = Minecraft.getInstance().player;

			if(player != null) {
				Inventory inventory = player.getInventory();

				if(currentHeldItem != -1 && inventory.selected != currentHeldItem) {
					inventory.selected = currentHeldItem;
					currentHeldItem = -1;
				}
			}

			if(hotbarChangeOpen && height < MAX_HEIGHT) {
				height += ANIM_PER_TICK;
				animating = true;
			} else if(!hotbarChangeOpen && height > 0) {
				height -= ANIM_PER_TICK;
				animating = true;
			} else animating = false;
		}

		private void acceptInput(int currInput) {
			Minecraft mc = Minecraft.getInstance();
			boolean down = changeHotbarKey.isDown();
			boolean wasDown = keyDown;
			keyDown = down;
			if(mc.isWindowActive()) {
				if(down && !wasDown)
					hotbarChangeOpen = !hotbarChangeOpen;
				else if(hotbarChangeOpen)
					for(int i = 0; i < 3; i++)
						if(isKeyDownOrFallback(mc.options.keyHotbarSlots[i], 49 + i, currInput)) {
							QuarkNetwork.sendToServer(new ChangeHotbarMessage(i + 1));
							hotbarChangeOpen = false;
							currentHeldItem = mc.player.getInventory().selected;
							return;
						}

			}
		}

		private boolean isKeyDownOrFallback(KeyMapping key, int input, int currInput) {
			if(key.isUnbound())
				return currInput != -1 && input == currInput;

			return key.isDown();
		}

		private float getRealHeight(float part) {
			if(!animating)
				return height;
			return height + part * ANIM_PER_TICK * (hotbarChangeOpen ? 1 : -1);
		}
	}
}
