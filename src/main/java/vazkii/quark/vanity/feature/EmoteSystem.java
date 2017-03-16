/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:53:17 (GMT)]
 */
package vazkii.quark.vanity.feature;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.client.gui.GuiButtonTranslucent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.client.emotes.EmoteCheer;
import vazkii.quark.vanity.client.emotes.EmoteClap;
import vazkii.quark.vanity.client.emotes.EmoteFacepalm;
import vazkii.quark.vanity.client.emotes.EmoteHeadbang;
import vazkii.quark.vanity.client.emotes.EmoteNo;
import vazkii.quark.vanity.client.emotes.EmotePoint;
import vazkii.quark.vanity.client.emotes.EmoteSalute;
import vazkii.quark.vanity.client.emotes.EmoteShrug;
import vazkii.quark.vanity.client.emotes.EmoteWave;
import vazkii.quark.vanity.client.emotes.EmoteYes;
import vazkii.quark.vanity.client.emotes.base.EmoteHandler;
import vazkii.quark.vanity.client.emotes.base.ModelAccessor;
import vazkii.quark.vanity.client.gui.GuiButtonEmote;
import vazkii.quark.vanity.command.CommandEmote;

public class EmoteSystem extends Feature {

	private static final int EMOTE_BUTTON_START = 1800;
	static boolean emotesVisible = false;

	private boolean enableKeybinds;

	@Override
	public void setupConfig() {
		enableKeybinds = loadPropBool("Enable Keybinds", "Should keybinds for emotes be generated? (They're all unbound by default)", true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		Tween.registerAccessor(ModelBiped.class, new ModelAccessor());

		MinecraftForge.EVENT_BUS.register(new EmoteHandler.TickHandler());

		EmoteHandler.emoteMap.put("wave", EmoteWave.class);
		EmoteHandler.emoteMap.put("salute", EmoteSalute.class);
		EmoteHandler.emoteMap.put("yes", EmoteYes.class);
		EmoteHandler.emoteMap.put("no", EmoteNo.class);
		EmoteHandler.emoteMap.put("cheer", EmoteCheer.class);
		EmoteHandler.emoteMap.put("clap", EmoteClap.class);
		EmoteHandler.emoteMap.put("point", EmotePoint.class);
		EmoteHandler.emoteMap.put("shrug", EmoteShrug.class);
		EmoteHandler.emoteMap.put("facepalm", EmoteFacepalm.class);
		EmoteHandler.emoteMap.put("headbang", EmoteHeadbang.class);

		if(enableKeybinds)
			ModKeybinds.initEmoteKeybinds();
	}

	@Override
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandEmote());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void initGui(GuiScreenEvent.InitGuiEvent.Post event) {
		GuiScreen gui = event.getGui();
		if(gui instanceof GuiChat) {
			List<GuiButton> list = event.getButtonList();
			list.add(new GuiButtonTranslucent(EMOTE_BUTTON_START, gui.width - 100, gui.height - 40, 100, 20, I18n.format("quark.gui.emotes")));

			int size = EmoteHandler.emoteMap.size() - 1;
			int i = 0;
			for(String key : EmoteHandler.emoteMap.keySet()) {
				int x = gui.width - 100;
				int y = gui.height - 61 - 21 * (size - i);

				GuiButton button = new GuiButtonEmote(EMOTE_BUTTON_START + i + 1, x, y, key);
				button.visible = emotesVisible;
				button.enabled = emotesVisible;
				list.add(button);
				i++;
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void performAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		GuiButton button = event.getButton();

		if(button.id == EMOTE_BUTTON_START) {
			event.getGui();
			List<GuiButton> list = event.getButtonList();

			for(GuiButton b : list)
				if(b instanceof GuiButtonEmote) {
					b.visible = !b.visible;
					b.enabled = !b.enabled;
				}

			emotesVisible = !emotesVisible;
		} else if(button instanceof GuiButtonEmote) {
			String emote = ((GuiButtonEmote) button).emote;
			Minecraft.getMinecraft().player.sendChatMessage("/emote " + emote);
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onKeyInput(KeyInputEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.inGameHasFocus && enableKeybinds) {
			for(KeyBinding key : ModKeybinds.emoteKeys.keySet())
				if(key.isKeyDown()) {
					String emote = ModKeybinds.emoteKeys.get(key);
					mc.player.sendChatMessage("/emote " + emote);
					return;
				}
		}
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
