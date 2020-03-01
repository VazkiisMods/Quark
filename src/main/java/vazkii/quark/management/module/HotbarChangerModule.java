package vazkii.quark.management.module;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.client.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.ChangeHotbarMessage;

@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class HotbarChangerModule extends Module {

	@OnlyIn(Dist.CLIENT)
	private static KeyBinding changeHotbarKey;

	private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

	private static final int ANIMATION_TIME = 10;
	private static final int MAX_HEIGHT = 90;
	private static final int ANIM_PER_TICK = MAX_HEIGHT / ANIMATION_TIME;

	public static int height = 0;
	public static int currentHeldItem = -1;
	public static boolean animating;
	public static boolean keyDown;
	public static boolean hotbarChangeOpen, shifting;

	@Override
	public void clientSetup() {
		changeHotbarKey = ModKeybindHandler.init("change_hotbar", "z", ModKeybindHandler.MISC_GROUP);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		acceptInput();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		acceptInput();
	}

	private void acceptInput() {
		Minecraft mc = Minecraft.getInstance();
		boolean down = changeHotbarKey.isKeyDown();
		boolean wasDown = keyDown;
		keyDown = down;
		if(mc.isGameFocused()) {
			if(down && !wasDown)
				hotbarChangeOpen = !hotbarChangeOpen;
			else if(hotbarChangeOpen)
				for(int i = 0; i < 3; i++)
					if(mc.gameSettings.keyBindsHotbar[i].isKeyDown()) {
						QuarkNetwork.sendToServer(new ChangeHotbarMessage(i + 1));
						hotbarChangeOpen = false;
						currentHeldItem = mc.player.inventory.currentItem;
						return;
					}

		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void hudPre(RenderGameOverlayEvent.Pre event) {
		float shift = -getRealHeight(event.getPartialTicks()) + 22;
		if(shift < 0)
			if(event.getType() == ElementType.HEALTH) {
				RenderSystem.translatef(0, shift, 0);
				shifting = true;
			} else if(shifting && (event.getType() == ElementType.DEBUG || event.getType() == ElementType.POTION_ICONS)) {
				RenderSystem.translatef(0, -shift, 0);
				shifting = false;
			}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void hudPost(RenderGameOverlayEvent.Post event) {
		if(height <= 0)
			return;

		Minecraft mc = Minecraft.getInstance();
		PlayerEntity player = mc.player;

		if(event.getType() == ElementType.HOTBAR) {
			MainWindow res = event.getWindow();
			float realHeight = getRealHeight(event.getPartialTicks());
			float xStart = res.getScaledWidth() / 2f - 91;
			float yStart = res.getScaledHeight() - realHeight;

			ItemRenderer render = mc.getItemRenderer();

			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			mc.textureManager.bindTexture(WIDGETS);
			for(int i = 0; i < 3; i++) {
				RenderSystem.pushMatrix();
				RenderSystem.color4f(1F, 1F, 1F, 0.75F);
				RenderSystem.translatef(xStart, yStart + i * 21, 0);
				mc.ingameGUI.blit(0, 0, 0, 0, 182, 22);
				RenderSystem.popMatrix();
			}

			for(int i = 0; i < 3; i++)
				mc.fontRenderer.drawStringWithShadow(TextFormatting.BOLD + Integer.toString(i + 1), xStart - 9, yStart + i * 21 + 7, 0xFFFFFF);

			RenderHelper.enableStandardItemLighting();

			RenderSystem.translatef(xStart, yStart, 0);
			for(int i = 0; i < 27; i++) {
				ItemStack invStack = player.inventory.getStackInSlot(i + 9);
				int x = (i % 9) * 20 + 3;
				int y = (i / 9) * 21 + 3;

				render.renderItemAndEffectIntoGUI(invStack, x, y);
				render.renderItemOverlays(mc.fontRenderer, invStack, x, y);
			}
			RenderHelper.disableStandardItemLighting();

			RenderSystem.popMatrix();
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			PlayerEntity player = Minecraft.getInstance().player;
			if(player != null && currentHeldItem != -1 && player.inventory.currentItem != currentHeldItem) {
				player.inventory.currentItem = currentHeldItem;
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

	private float getRealHeight(float part) {
		if(!animating)
			return height;
		return height + part * ANIM_PER_TICK * (hotbarChangeOpen ? 1 : -1);
	}

}
