package vazkii.quark.content.client.module;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.InputConstants.Type;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenEvent.KeyPressed;
import net.minecraftforge.client.event.ScreenEvent.MouseButtonPressed;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZKeyMapping;

@LoadModule(category = "client", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class BackButtonKeybindModule extends QuarkModule {

	@OnlyIn(Dist.CLIENT)
	private static KeyMapping backKey;

	@OnlyIn(Dist.CLIENT)
	private static List<GuiEventListener> listeners;

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void registerKeybinds(ZKeyMapping event) {
		backKey = ModKeybindHandler.initMouse(event, "back", 4, ModKeybindHandler.MISC_GROUP, (modifier, key) -> key.getType() != Type.MOUSE || key.getValue() != 0);
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void openGui(ScreenEvent.Init event) {
		listeners = event.getListenersList();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(KeyPressed.Post event) {
		if(backKey.getKey().getType() == Type.KEYSYM && event.getKeyCode() == backKey.getKey().getValue())
			clicc();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMouseInput(MouseButtonPressed.Post event) {
		int btn = event.getButton();
		if(backKey.getKey().getType() == Type.MOUSE && btn != GLFW.GLFW_MOUSE_BUTTON_LEFT && btn == backKey.getKey().getValue())
			clicc();
	}

	private void clicc() {
		ImmutableSet<String> buttons = ImmutableSet.of(
				I18n.get("gui.back"),
				I18n.get("gui.done"),
				I18n.get("gui.cancel"),
				I18n.get("gui.toTitle"),
				I18n.get("gui.toMenu"),
				I18n.get("quark.gui.config.save"));

		// Iterate this way to ensure we match the more important back buttons first
		for(String b : buttons)
			for(GuiEventListener listener : listeners) {
				if(listener instanceof Button w) {
					if(w.getMessage() != null && w.getMessage().getString().equals(b) && w.visible && w.active) {
						w.onClick(0, 0);
						return;
					}
				}
			}

		Minecraft mc = Minecraft.getInstance();
		if(mc.level != null)
			mc.setScreen(null);
	}

}
