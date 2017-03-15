package vazkii.quark.management.feature;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageChangeHotbar;

public class ChangeHotbarKeybind extends Feature {

	private static final int[] BAR_KEYS = new int[] {
			Keyboard.KEY_1, Keyboard.KEY_2, Keyboard.KEY_3
	};

	private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");

	private static final int ANIMATION_TIME = 10;
	private static final int MAX_HEIGHT = 90;
	private static final int ANIM_PER_TICK = MAX_HEIGHT / ANIMATION_TIME;

	int height = 0;
	int currentHeldItem;
	boolean animating;
	boolean keyDown;
	boolean hotbarChangeOpen, shifting;

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		ModKeybinds.initChangeHotbarKey();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		boolean down = ModKeybinds.changeHotbarKey.isKeyDown();
		boolean wasDown = keyDown;
		keyDown = down;
		if(mc.inGameHasFocus) {
			if(down && !wasDown)
				hotbarChangeOpen = !hotbarChangeOpen;
			else if(hotbarChangeOpen)
				for(int i = 0; i < BAR_KEYS.length; i++)
					if(Keyboard.isKeyDown(BAR_KEYS[i])) {
						NetworkHandler.INSTANCE.sendToServer(new MessageChangeHotbar(i + 1));
						hotbarChangeOpen = false;
						currentHeldItem = mc.player.inventory.currentItem;
						return;
					}

		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void hudPre(RenderGameOverlayEvent.Pre event) {
		float shift = -getRealHeight(event.getPartialTicks()) + 22;
		if(shift < 0)
			if(event.getType() == ElementType.HEALTH) {
				GlStateManager.translate(0, shift, 0);
				shifting = true;
			} else if(shifting && (event.getType() == ElementType.DEBUG || event.getType() == ElementType.POTION_ICONS)) {
				GlStateManager.translate(0, -shift, 0);
				shifting = false;
			}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void hudPost(RenderGameOverlayEvent.Post event) {
		if(height <= 0)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		if(event.getType() == ElementType.HOTBAR) {
			ScaledResolution res = event.getResolution();
			float realHeight = getRealHeight(event.getPartialTicks());
			float xStart = res.getScaledWidth() / 2 - 91;
			float yStart = res.getScaledHeight() - realHeight;

			RenderItem render = mc.getRenderItem();

			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			mc.renderEngine.bindTexture(WIDGETS);
			for(int i = 0; i < 3; i++) {
				GlStateManager.color(1F, 1F, 1F, 0.75F);
				mc.ingameGUI.drawTexturedModalRect(xStart, yStart + i * 21, 0, 0, 182, 22);
			}

			for(int i = 0; i < 3; i++)
				mc.fontRendererObj.drawStringWithShadow(TextFormatting.BOLD + Integer.toString(i + 1), xStart - 9, yStart + i * 21 + 7, 0xFFFFFF);

			RenderHelper.enableGUIStandardItemLighting();

			GlStateManager.translate(xStart, yStart, 0);
			for(int i = 0; i < 27; i++) {
				ItemStack invStack = player.inventory.getStackInSlot(i + 9);
				int x = (i % 9) * 20 + 3;
				int y = (i / 9) * 21 + 3;

				render.renderItemAndEffectIntoGUI(invStack, x, y);
				render.renderItemOverlays(mc.fontRendererObj, invStack, x, y);
			}

			GlStateManager.popMatrix();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onTick(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			EntityPlayer player = Minecraft.getMinecraft().player;
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

	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

}
