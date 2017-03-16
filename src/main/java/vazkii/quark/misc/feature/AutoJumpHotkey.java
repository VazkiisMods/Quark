/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/07/2016, 17:13:46 (GMT)]
 */
package vazkii.quark.misc.feature;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.module.Feature;

public class AutoJumpHotkey extends Feature {

	private static final float DISPLAY_TIME = 10F;
	private static final float FADE_TIME = 10F;
	private static final float TOTAL_TIME = DISPLAY_TIME + FADE_TIME;

	private static final ResourceLocation AUTOJUMP_OFF = new ResourceLocation("quark", "textures/misc/autojump_off.png");
	private static final ResourceLocation AUTOJUMP_ON = new ResourceLocation("quark", "textures/misc/autojump_on.png");

	int hudTime;

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		ModKeybinds.initAutoJumpKey();
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		boolean down = ModKeybinds.autoJumpKey.isKeyDown();
		if(mc.inGameHasFocus && down) {
			boolean b = mc.gameSettings.getOptionOrdinalValue(Options.AUTO_JUMP);
			int i = b ? 1 : 0;
			mc.gameSettings.setOptionValue(Options.AUTO_JUMP, ~i & 1);
			hudTime = (int) TOTAL_TIME;
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.END && hudTime > 0)
			hudTime--;
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() != ElementType.ALL || hudTime == 0)
			return;

		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution res = event.getResolution();
		int x = res.getScaledWidth() / 2 - 16;
		int y = res.getScaledHeight() - 100;
		boolean autojumpEnabled = mc.gameSettings.getOptionOrdinalValue(Options.AUTO_JUMP);

		Math.min(FADE_TIME, hudTime + DISPLAY_TIME);
		float alpha = (hudTime + event.getPartialTicks()) / FADE_TIME;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1F, 1F, 1F, alpha);
		mc.renderEngine.bindTexture(autojumpEnabled ? AUTOJUMP_ON : AUTOJUMP_OFF);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 32, 32, 32, 32);
		GlStateManager.popMatrix();
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
