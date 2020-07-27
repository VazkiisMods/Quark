package vazkii.quark.base.client;

import java.io.File;
import java.io.IOException;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldSelectionScreen;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.base.Quark;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class BLMHandler {
	private static final long KILLSWITCH = 1598918400000L; // 1 Sep 2020
	
	private static boolean didTheThing = false;

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if(!didTheThing && isUnitedStatesOfAmerica(mc)) {
			Screen curr = mc.currentScreen;

			if(curr instanceof WorldSelectionScreen || curr instanceof MultiplayerScreen) {
				if(!getMarker().exists() && System.currentTimeMillis() < KILLSWITCH)
					mc.displayGuiScreen(new BLMScreen(curr));
				
				didTheThing = true;
			}
		}
	}
	
	private static boolean isUnitedStatesOfAmerica(Minecraft mc) {
		return mc.getLanguageManager() != null
				&& mc.getLanguageManager().getCurrentLanguage() != null
				&& mc.getLanguageManager().getCurrentLanguage().getName() != null
				&& mc.getLanguageManager().getCurrentLanguage().getName().equals("English")
				&& mc.getLanguageManager().getCurrentLanguage().getRegion().equals("US");
	}
	
	private static File getMarker() {
		File root = new File(".");
		File ourDir = new File(root, "saves/.quark");
		if(!ourDir.exists())
			ourDir.mkdirs();
		
		return new File(ourDir, ".blm_marker");
	}

	@OnlyIn(Dist.CLIENT)
	private static class BLMScreen extends Screen {

		private static final int TOTAL_TIME = (8 * 60 * 20) + (46 * 20);

		final Screen parent;
		int ticksElapsed = 0;
		boolean attemptedEsc = false;
		boolean openedWebsite = false;

		protected BLMScreen(Screen parent) {
			super(new StringTextComponent(""));
			this.parent = parent;
		}
		
		@Override
		public void render(MatrixStack mstack, int mx, int my, float pticks) {
			super.render(mstack, mx, my, pticks);

			fill(mstack, 0, 0, width, height, 0xFF000000);
			int middle = width / 2;

			int timeLeft = TOTAL_TIME - ticksElapsed;
			int secs = timeLeft / 20;
			int mins = secs / 60; 
			secs -= (mins * 60);
			
			if(attemptedEsc || ticksElapsed > 500) {
				RenderSystem.pushMatrix();
				RenderSystem.scalef(3, 3, 3);
				drawCenteredString(mstack, font, String.format("%dm%02ds", mins, secs), middle / 3, 10, 0xFFFFFF);
				RenderSystem.popMatrix();
			}
			
			String[] message = new String[0];
			int dist = 15;

			if(attemptedEsc) {
				message = new String[] {
						"Before you go, remember: You can skip this.",
						"George Floyd couldn't. Black americans can't.",
						"While you're waiting to play, they're waiting to die.",
						"",
						"Black people are disproportionately killed by the police",
						"in the United States. It's time to stand up.",
						"Click anywhere on screen to find out how to help.",
						"#BlackLivesMatter",
						"",
						"(Press ESC again to leave, this mesasge won't show again)"
				};
			} else {
				message = new String[] {
						"Before you start playing, please read this message provided by Quark.",
						"George Floyd was killed by a police officer, who knelt on him for 8m46s.",
						"He was accused of paying with a counterfeit $20 bill.",
						"Innocent black lives are being taken by police officers all over the USA.",
						"It's time to make history.",
						"",
						"Click anywhere on screen to find out how to help.",
						"(You may press ESC to skip)",
						"#BlackLivesMatter",
						
				};
			}
			
			for(int i = 0; i < message.length; i++) {
				if(attemptedEsc || (ticksElapsed - 20) > i * 70) 
					drawCenteredString(mstack, font, message[i], middle, 70 + i * dist, 0xFFFFFF);
			}
		}

		@Override
		public void tick() {
			super.tick();

			ticksElapsed++;
			if(ticksElapsed > TOTAL_TIME)
				leave();
		}
		
		private void leave() {
			try {
				getMarker().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			minecraft.displayGuiScreen(parent);
		}

		@Override
		public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
			if(p_keyPressed_1_ == 256) {
				if(!attemptedEsc)
					attemptedEsc = true;
				else {
					leave();
					return true;
				}
				
				return false;
			}

			return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}

		@Override
		public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
			if(ticksElapsed < 400)
				return false;
			
			if(p_mouseClicked_5_ == 0 && !openedWebsite) {
				minecraft.displayGuiScreen(new ConfirmOpenLinkScreen(this::consume, "https://blacklivesmatter.carrd.co/", true));
				return true;
			}
			
			return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
		}
		
		private void consume(boolean b) {
			minecraft.displayGuiScreen(this);
			if(b)
				Util.getOSType().openURI("https://blacklivesmatter.carrd.co/");
			openedWebsite = b;
		}
		
	}

}
