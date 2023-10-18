package vazkii.quark.content.client.module;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.experimental.module.OverlayShaderModule;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZKeyMapping;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.function.Predicate;

@LoadModule(category = "client", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class CameraModule extends QuarkModule {

	@Config(description = "Date format that will be displayed in screenshots. Must be a valid one (i.e. MM/dd/yyyy)")
	@Config.Condition(DatePredicate.class)
	private static String dateFormat = "MM/dd/yyyy";

	private static final int RULER_COLOR = 0x33000000;

	private static final int RULERS = 4;
	private static final int BORERS = 6;
	private static final int OVERLAYS = 5;


	private static class DatePredicate implements Predicate<Object> {
		@Override
		public boolean test(Object o) {
			if (o instanceof String s) {
				try {
					new SimpleDateFormat(s);
					return true;
				} catch (IllegalArgumentException ignored) {
					// NO-OP
				}
			}
			return false;
		}
	}

	private static final ResourceLocation[] SHADERS = new ResourceLocation[] {
			null,
			new ResourceLocation(Quark.MOD_ID, "shaders/post/grayscale.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/sepia.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/desaturate.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/oversaturate.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/cool.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/warm.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/conjugate.json"),

			new ResourceLocation(Quark.MOD_ID, "shaders/post/redfocus.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/greenfocus.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/bluefocus.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/yellowfocus.json"),

			new ResourceLocation("shaders/post/bumpy.json"),
			new ResourceLocation("shaders/post/notch.json"),
			new ResourceLocation("shaders/post/creeper.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/enderman.json"),

			new ResourceLocation(Quark.MOD_ID, "shaders/post/bits.json"),
			new ResourceLocation("shaders/post/blobs.json"),
			new ResourceLocation("shaders/post/pencil.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/watercolor.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/monochrome.json"),
			new ResourceLocation("shaders/post/sobel.json"),

			new ResourceLocation(Quark.MOD_ID, "shaders/post/colorblind/deuteranopia.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/colorblind/protanopia.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/colorblind/tritanopia.json"),
			new ResourceLocation(Quark.MOD_ID, "shaders/post/colorblind/achromatopsia.json")
	};

	@OnlyIn(Dist.CLIENT)
	private static KeyMapping cameraModeKey;

	private static int currentHeldItem = -1;
	private static int currShader = 0;
	private static int currRulers = 0;
	private static int currBorders = 0;
	private static int currOverlay = 0;
	private static boolean queuedRefresh = false;
	private static boolean queueScreenshot = false;
	private static boolean screenshotting = false;

	private static boolean cameraMode;

	@LoadEvent
	public void registerKeybinds(ZKeyMapping event) {
		cameraModeKey = ModKeybindHandler.init(event, "camera_mode", "f12", ModKeybindHandler.MISC_GROUP);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void screenshotTaken(ScreenshotEvent event) {
		screenshotting = false;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void keystroke(Key event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.level != null && event.getAction() == GLFW.GLFW_PRESS) {
			if(cameraModeKey.isDown()) {
				cameraMode = !cameraMode;
				queuedRefresh = true;
				return;
			}

			if(cameraMode && mc.screen == null) {
				int key = event.getKey();

				boolean affected = false;
				boolean sneak = mc.player.isDiscrete();
				switch(key) {
				case 49: // 1
					currShader = cycle(currShader, SHADERS.length, sneak);
					affected = true;
					break;
				case 50: // 2
					currRulers = cycle(currRulers, RULERS, sneak);
					affected = true;
					break;
				case 51: // 3
					currBorders = cycle(currBorders, BORERS, sneak);
					affected = true;
					break;
				case 52: // 4
					currOverlay = cycle(currOverlay, OVERLAYS, sneak);
					affected = true;
					break;
				case 53: // 5
					if(sneak) {
						currShader = 0;
						currRulers = 0;
						currBorders = 0;
						currOverlay = 0;
						affected = true;
					}
					break;
				case 257: // ENTER
					if(!queueScreenshot && !screenshotting)
						mc.getSoundManager().play(SimpleSoundInstance.forUI(QuarkSounds.ITEM_CAMERA_SHUTTER, 1.0F));

					queueScreenshot = true;
				}

				if(affected) {
					queuedRefresh = true;
					currentHeldItem = mc.player.getInventory().selected;
				}
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderTick(RenderTickEvent event) {
		Minecraft mc = Minecraft.getInstance();

		Player player = mc.player;
		if(player != null) {
			Inventory inventory = player.getInventory();
			if(currentHeldItem != -1 && inventory.selected != currentHeldItem) {
				inventory.selected = currentHeldItem;
				currentHeldItem = -1;
			}
		}

		if(mc.level == null) {
			cameraMode = false;
			queuedRefresh = true;
		} else if(queuedRefresh)
			refreshShader();

		if(event.phase == Phase.END && cameraMode && mc.screen == null) {
			if(queueScreenshot)
				screenshotting = true;

			PoseStack stack = new PoseStack();
			renderCameraHUD(mc, stack);

			if(queueScreenshot) {
				queueScreenshot = false;
				Screenshot.grab(mc.gameDirectory, mc.getMainRenderTarget(), (msg) -> mc.execute(() -> mc.gui.getChat().addMessage(msg)));
			}
		}
	}

	private static void renderCameraHUD(Minecraft mc, PoseStack matrix) {
		Window mw = mc.getWindow();
		int twidth = mw.getGuiScaledWidth();
		int theight = mw.getGuiScaledHeight();
		int width = twidth;
		int height = theight;

		int paddingHoriz = 0;
		int paddingVert = 0;
		int paddingColor = 0xFF000000;

		double targetAspect = -1;

		switch (currBorders) {
			case 1 -> // Square
					targetAspect = 1;
			case 2 -> // 4:3
					targetAspect = 4.0 / 3.0;
			case 3 -> // 16:9
					targetAspect = 16.0 / 9.0;
			case 4 -> // 21:9
					targetAspect = 21.0 / 9.0;
			case 5 -> { // Polaroid
				int border = (int) (20.0 * ((double) (twidth * theight) / 518400));
				paddingHoriz = border;
				paddingVert = border;
				paddingColor = 0xFFFFFFFF;
			}
		}

		if(targetAspect > 0) {
			double currAspect = (double) width / (double) height;

			if(currAspect > targetAspect) {
				int desiredWidth = (int) ((double) height * targetAspect);
				paddingHoriz = (width - desiredWidth) / 2;
			} else if (currAspect < targetAspect) {
				int desiredHeight = (int) ((double) width / targetAspect);
				paddingVert = (height - desiredHeight) / 2;
			}
		}

		width -= (paddingHoriz * 2);
		height -= (paddingVert * 2);

		// =============================================== DRAW BORDERS ===============================================
		if(paddingHoriz > 0) {
			Screen.fill(matrix, 0, 0, paddingHoriz, theight, paddingColor);
			Screen.fill(matrix, twidth - paddingHoriz, 0, twidth, theight, paddingColor);
		}

		if(paddingVert > 0) {
			Screen.fill(matrix, 0, 0, twidth, paddingVert, paddingColor);
			Screen.fill(matrix, 0, theight - paddingVert, twidth, theight, paddingColor);
		}

		// =============================================== DRAW OVERLAYS ===============================================
		String overlayText = "";
		boolean overlayShadow = true;
		double overlayScale = 2.0;
		int overlayColor = 0xFFFFFFFF;
		int overlayX = -1;
		int overlayY = -1;

		switch (currOverlay) {
			case 1 -> { // Date
				overlayText = new SimpleDateFormat(dateFormat).format(new Date(System.currentTimeMillis()));
				overlayColor = 0xf77700;
			}
			case 2 -> { // Postcard
				String worldName = "N/A";
				if (mc.getSingleplayerServer() != null)
					worldName = mc.getSingleplayerServer().name();
				else if (mc.getCurrentServer() != null)
					worldName = mc.getCurrentServer().name;
				overlayText = I18n.get("quark.camera.greetings", worldName);
				overlayX = paddingHoriz + 20;
				overlayY = paddingVert + 20;
				overlayScale = 3;
				overlayColor = 0xef5425;
			}
			case 3 -> { // Watermark
				overlayText = mc.player.getGameProfile().getName();
				overlayScale = 6;
				overlayShadow = false;
				overlayColor = 0x44000000;
			}
			case 4 -> { // Held Item
				overlayText = mc.player.getMainHandItem().getHoverName().getString();
				overlayX = twidth / 2 - mc.font.width(overlayText);
				overlayY = paddingVert + 40;
			}
		}

		if(overlayX == -1)
			overlayX = twidth - paddingHoriz - mc.font.width(overlayText) * (int) overlayScale - 40;
		if(overlayY == -1)
			overlayY = theight - paddingVert - 10 - (10 * (int) overlayScale);


		if(!overlayText.isEmpty()) {
			matrix.pushPose();
			matrix.translate(overlayX, overlayY, 0);
			matrix.scale((float) overlayScale, (float) overlayScale, 1.0F);
			if(overlayShadow)
				mc.font.drawShadow(matrix, overlayText, 0, 0, overlayColor);
			else mc.font.draw(matrix, overlayText, 0, 0, overlayColor);
			matrix.popPose();
		}

		if(!screenshotting) {
			// =============================================== DRAW RULERS ===============================================
			matrix.pushPose();
			matrix.translate(paddingHoriz, paddingVert, 0);
			switch (currRulers) {
				case 1 -> { // Rule of Thirds
					vruler(matrix, width / 3, height);
					vruler(matrix, width / 3 * 2, height);
					hruler(matrix, height / 3, width);
					hruler(matrix, height / 3 * 2, width);
				}
				case 2 -> { // Golden Ratio
					double phi1 = 1 / 2.61;
					double phi2 = 1.61 / 2.61;
					vruler(matrix, (int) (width * phi1), height);
					vruler(matrix, (int) (width * phi2), height);
					hruler(matrix, (int) (height * phi1), width);
					hruler(matrix, (int) (height * phi2), width);
				}
				case 3 -> { // Center
					vruler(matrix, width / 2, height);
					hruler(matrix, height / 2, width);
				}
			}
			matrix.popPose();

			int left = 30;
			int top = theight - 65;

			// =============================================== DRAW SETTINGS ===============================================
			ResourceLocation shader = SHADERS[currShader];
			String text = "none";
			if(shader != null)
				text = shader.getPath().replaceAll(".+/(.+)\\.json", "$1");
			text = ChatFormatting.BOLD + "[1] " + ChatFormatting.RESET + I18n.get("quark.camera.filter") + ChatFormatting.GOLD + I18n.get("quark.camera.filter." + text);
			mc.font.drawShadow(matrix, text, left, top, 0xFFFFFF);

			text = ChatFormatting.BOLD + "[2] " + ChatFormatting.RESET + I18n.get("quark.camera.rulers") + ChatFormatting.GOLD + I18n.get("quark.camera.rulers" + currRulers);
			mc.font.drawShadow(matrix, text, left, top + 12, 0xFFFFFF);

			text = ChatFormatting.BOLD + "[3] " + ChatFormatting.RESET + I18n.get("quark.camera.borders") + ChatFormatting.GOLD + I18n.get("quark.camera.borders" + currBorders);
			mc.font.drawShadow(matrix, text, left, top + 24, 0xFFFFFF);

			text = ChatFormatting.BOLD + "[4] " + ChatFormatting.RESET + I18n.get("quark.camera.overlay") + ChatFormatting.GOLD + I18n.get("quark.camera.overlay" + currOverlay);
			mc.font.drawShadow(matrix, text, left, top + 36, 0xFFFFFF);

			text = ChatFormatting.BOLD + "[5] " + ChatFormatting.RESET + I18n.get("quark.camera.reset");
			mc.font.drawShadow(matrix, text, left, top + 48, 0xFFFFFF);

			text = ChatFormatting.AQUA + I18n.get("quark.camera.header");
			mc.font.drawShadow(matrix, text, (float) (twidth / 2 - mc.font.width(text) / 2), 6, 0xFFFFFF);

			text = I18n.get("quark.camera.info", Component.keybind("quark.keybind.camera_mode").getString());
			mc.font.drawShadow(matrix, text, (float) (twidth / 2 - mc.font.width(text) / 2), 16, 0xFFFFFF);

			ResourceLocation CAMERA_TEXTURE = new ResourceLocation(Quark.MOD_ID, "textures/misc/camera.png");
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, CAMERA_TEXTURE);
			Screen.blit(matrix, left - 22, top + 18, 0, 0, 0, 16, 16, 16, 16);
		}
	}

	private static void refreshShader() {
		if(queuedRefresh)
			queuedRefresh = false;

		Minecraft mc = Minecraft.getInstance();
		GameRenderer render = mc.gameRenderer;
		mc.options.hideGui = cameraMode;

		if(cameraMode) {
			ResourceLocation shader = SHADERS[currShader];

			if(shader != null) {
				render.loadEffect(shader);
				return;
			}
		}
		else if(ModuleLoader.INSTANCE.isModuleEnabled(OverlayShaderModule.class)) {
			for(ResourceLocation l : SHADERS) {
				if(l != null && l.getPath().contains(OverlayShaderModule.shader + ".json")) {
					render.loadEffect(l);
					return;
				}
			}
		}

		render.checkEntityPostEffect(null);
	}

	private static void vruler(PoseStack matrix, int x, int height) {
		Screen.fill(matrix, x, 0, x + 1, height, RULER_COLOR);
	}

	private static void hruler(PoseStack matrix, int y, int width) {
		Screen.fill(matrix, 0, y, width, y + 1, RULER_COLOR);
	}

	private static int cycle(int curr, int max, boolean neg) {
		int val = curr + (neg ? -1 : 1);
		if(val < 0)
			val = max - 1;
		else if(val >= max)
			val = 0;

		return val;
	}

}

