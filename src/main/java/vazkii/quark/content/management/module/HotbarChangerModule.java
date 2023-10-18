package vazkii.quark.content.management.module;

import org.lwjgl.opengl.GL11;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.ChangeHotbarMessage;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZKeyMapping;

@LoadModule(category = "management", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class HotbarChangerModule extends QuarkModule {

	@OnlyIn(Dist.CLIENT)
	private static KeyMapping changeHotbarKey;

	private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

	private static final int ANIMATION_TIME = 10;
	private static final int MAX_HEIGHT = 90;
	private static final int ANIM_PER_TICK = MAX_HEIGHT / ANIMATION_TIME;

	public static int height = 0;
	public static int currentHeldItem = -1;
	public static boolean animating;
	public static boolean keyDown;
	public static boolean hotbarChangeOpen, shifting;

	@LoadEvent
	public void registerKeybinds(ZKeyMapping event) {
		changeHotbarKey = ModKeybindHandler.init(event, "change_hotbar", "z", ModKeybindHandler.MISC_GROUP);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMouseInput(InputEvent.MouseButton event) {
		acceptInput(-1);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(InputEvent.Key event) {
		acceptInput(event.getKey());
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

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void hudPre(RenderGuiOverlayEvent.Pre event) {
		float shift = -getRealHeight(event.getPartialTick()) + 22;
		if(shift < 0) {
			NamedGuiOverlay overlay = event.getOverlay();
			if(overlay == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
				event.getPoseStack().translate(0, shift, 0);
				shifting = true;
			} else if(shifting && (overlay == VanillaGuiOverlay.DEBUG_TEXT.type() || overlay == VanillaGuiOverlay.POTION_ICONS.type())) {
				event.getPoseStack().translate(0, -shift, 0);
				shifting = false;
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void hudPost(RenderGuiOverlayEvent.Post event) {
		if(height <= 0)
			return;

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		PoseStack matrix = event.getPoseStack();

		if(event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
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
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			Player player = Minecraft.getInstance().player;
			
			if(player != null) {
				Inventory inventory = player.getInventory();

				if(currentHeldItem != -1 && inventory.selected != currentHeldItem) {
					inventory.selected = currentHeldItem;
					currentHeldItem = -1;	
				}
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

	private float getRealHeight(float part) {
		if(!animating)
			return height;
		return height + part * ANIM_PER_TICK * (hotbarChangeOpen ? 1 : -1);
	}

}
