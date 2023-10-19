package vazkii.quark.content.tweaks.module;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.aurelienribon.tweenengine.Tween;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.RequestEmoteMessage;
import vazkii.quark.content.tweaks.client.emote.CustomEmoteIconResourcePack;
import vazkii.quark.content.tweaks.client.emote.EmoteBase;
import vazkii.quark.content.tweaks.client.emote.EmoteDescriptor;
import vazkii.quark.content.tweaks.client.emote.EmoteHandler;
import vazkii.quark.content.tweaks.client.emote.ModelAccessor;
import vazkii.quark.content.tweaks.client.screen.widgets.EmoteButton;
import vazkii.quark.content.tweaks.client.screen.widgets.TranslucentButton;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientModulesReady;
import vazkii.zeta.event.client.ZClientSetup;
import vazkii.zeta.event.client.ZConfigChangedClient;
import vazkii.zeta.event.client.ZKeyMapping;

@LoadModule(category = "tweaks", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class EmotesModule extends QuarkModule {

	private static final Set<String> DEFAULT_EMOTE_NAMES = ImmutableSet.of(
			"no",
			"yes",
			"wave",
			"salute",
			"cheer",
			"clap",
			"think",
			"point",
			"shrug",
			"headbang",
			"weep",
			"facepalm");

	private static final Set<String> PATREON_EMOTES = ImmutableSet.of(
			"dance",
			"tpose",
			"dab",
			"jet",
			"exorcist",
			"zombie");

	public static final int EMOTE_BUTTON_WIDTH = 25;
	public static final int EMOTES_PER_ROW = 3;

	@Config(description = "The enabled default emotes. Remove from this list to disable them. You can also re-order them, if you feel like it.")
	public static List<String> enabledEmotes = Lists.newArrayList(DEFAULT_EMOTE_NAMES);

	@Config(description = "The list of Custom Emotes to be loaded.\nWatch the tutorial on Custom Emotes to learn how to make your own: https://youtu.be/ourHUkan6aQ")
	public static List<String> customEmotes = Lists.newArrayList();

	@Config(description = "Enable this to make custom emotes read the file every time they're triggered so you can edit on the fly.\nDO NOT ship enabled this in a modpack, please.")
	public static boolean customEmoteDebug = false;
	
	@Config public static int buttonShiftX = 0;
	@Config public static int buttonShiftY = 0;

	public static boolean emotesVisible = false;
	public static File emotesDir;

	@OnlyIn(Dist.CLIENT)
	public static CustomEmoteIconResourcePack resourcePack;

	@OnlyIn(Dist.CLIENT)
	private static Map<KeyMapping, String> emoteKeybinds;

	@LoadEvent
	public void constructClient(ZClientModulesReady e) {
		Minecraft mc = Minecraft.getInstance();
		if (mc == null)
			return; // Mojang datagen has no client instance available

		emotesDir = new File(mc.gameDirectory, "/config/quark_emotes");
		if(!emotesDir.exists())
			emotesDir.mkdirs();

		mc.getResourcePackRepository().addPackFinder(new RepositorySource() {

			@Override
			public void loadPacks(@Nonnull Consumer<Pack> packConsumer, @Nonnull Pack.PackConstructor packInfoFactory) {
				resourcePack = new CustomEmoteIconResourcePack();

				String name = "quark:emote_resources";
				Pack t = Pack.create(name, true, () -> resourcePack, packInfoFactory, Pack.Position.TOP, tx->tx);
				packConsumer.accept(t);
			}
		});
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		Tween.registerAccessor(HumanoidModel.class, ModelAccessor.INSTANCE);
	}

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void registerKeybinds(ZKeyMapping event) {
		int sortOrder = 0;

		emoteKeybinds = new HashMap<>();
		for (String s : DEFAULT_EMOTE_NAMES)
			emoteKeybinds.put(ModKeybindHandler.init(event, "quark.emote." + s, null, "", ModKeybindHandler.EMOTE_GROUP, sortOrder++, false), s);
		for (String s : PATREON_EMOTES)
			emoteKeybinds.put(ModKeybindHandler.init(event, "patreon_emote." + s, null, ModKeybindHandler.EMOTE_GROUP, sortOrder++), s);
	}

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void configChangedClient(ZConfigChangedClient e) {
		EmoteHandler.clearEmotes();

		for(String s : enabledEmotes) {
			if (DEFAULT_EMOTE_NAMES.contains(s))
				EmoteHandler.addEmote(s);
		}

		for(String s : PATREON_EMOTES)
			EmoteHandler.addEmote(s);

		for(String s : customEmotes)
			EmoteHandler.addCustomEmote(s);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void initGui(ScreenEvent.Init.Post event) {
		Screen gui = event.getScreen();
		if(gui instanceof ChatScreen) {
			Map<Integer, List<EmoteDescriptor>> descriptorSorting = new TreeMap<>();

			for (EmoteDescriptor desc : EmoteHandler.emoteMap.values()) {
				if (desc.getTier() <= ContributorRewardHandler.localPatronTier) {
					List<EmoteDescriptor> descriptors = descriptorSorting.computeIfAbsent(desc.getTier(), k -> new LinkedList<>());

					descriptors.add(desc);
				}
			}

			int rows = 0;
			int row = 0;
			int tierRow, rowPos;

			Minecraft mc = Minecraft.getInstance();
			boolean expandDown = mc.options.showSubtitles().get();

			Set<Integer> keys = descriptorSorting.keySet();
			for(int tier : keys) {
				List<EmoteDescriptor> descriptors = descriptorSorting.get(tier);
				if (descriptors != null) {
					rows += descriptors.size() / 3;
					if (descriptors.size() % 3 != 0)
						rows++;
				}
			}

			int buttonX = buttonShiftX;
			int buttonY = (expandDown ? 2 : gui.height - 40) + buttonShiftY;

			List<Button> emoteButtons = new LinkedList<>();
			for (int tier : keys) {
				rowPos = 0;
				tierRow = 0;
				List<EmoteDescriptor> descriptors = descriptorSorting.get(tier);
				if (descriptors != null) {
					for (EmoteDescriptor desc : descriptors) {
						int rowSize = Math.min(descriptors.size() - tierRow * EMOTES_PER_ROW, EMOTES_PER_ROW);

						int x = buttonX + gui.width - (EMOTE_BUTTON_WIDTH * (EMOTES_PER_ROW + 1)) + (((rowPos + 1) * 2 + EMOTES_PER_ROW - rowSize) * EMOTE_BUTTON_WIDTH / 2 + 1);
						int y = buttonY + (EMOTE_BUTTON_WIDTH * (rows - row)) * (expandDown ? 1 : -1);

						Button button = new EmoteButton(x, y, desc, (b) -> {
							String name = desc.getRegistryName();
							QuarkNetwork.sendToServer(new RequestEmoteMessage(name));
						});
						emoteButtons.add(button);

						button.visible = emotesVisible;
						button.active = emotesVisible;
						event.addListener(button);

						if (++rowPos == EMOTES_PER_ROW) {
							tierRow++;
							row++;
							rowPos = 0;
						}
					}
				}
				if (rowPos != 0)
					row++;
			}

			event.addListener(new TranslucentButton(buttonX + gui.width - 1 - EMOTE_BUTTON_WIDTH * EMOTES_PER_ROW, buttonY, EMOTE_BUTTON_WIDTH * EMOTES_PER_ROW, 20,
					Component.translatable("quark.gui.button.emotes"),
					(b) -> {
						for(Button bt : emoteButtons)
							if(bt instanceof EmoteButton) {
								bt.visible = !bt.visible;
								bt.active = !bt.active;
							}

						emotesVisible = !emotesVisible;
					}));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(InputEvent.Key event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.isWindowActive()) {
			for(KeyMapping key : emoteKeybinds.keySet()) {
				if (key.isDown()) {
					String emote = emoteKeybinds.get(key);
					QuarkNetwork.sendToServer(new RequestEmoteMessage(emote));
					return;
				}
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void drawHUD(RenderGuiOverlayEvent.Post event) {
		if(event.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type()) {
			Minecraft mc = Minecraft.getInstance();
			Window res = event.getWindow();
			PoseStack stack = event.getPoseStack();
			EmoteBase emote = EmoteHandler.getPlayerEmote(mc.player);
			if(emote != null && emote.timeDone < emote.totalTime) {
				ResourceLocation resource = emote.desc.texture;
				int x = res.getGuiScaledWidth() / 2 - 16;
				int y = res.getGuiScaledHeight() / 2 - 60;
				float transparency = 1F;
				float tween = 5F;

				if(emote.timeDone < tween)
					transparency = emote.timeDone / tween;
				else if(emote.timeDone > emote.totalTime - tween)
					transparency = (emote.totalTime - emote.timeDone) / tween;

				stack.pushPose();
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, transparency);
				RenderSystem.setShaderTexture(0, resource);

				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();

				Screen.blit(stack, x, y, 0, 0, 32, 32, 32, 32);
				RenderSystem.enableBlend();

				String name = I18n.get(emote.desc.getTranslationKey());
				mc.font.drawShadow(stack, name, res.getGuiScaledWidth() / 2f - mc.font.width(name) / 2f, y + 34, 0xFFFFFF + (((int) (transparency * 255F)) << 24));
				stack.popPose();
			}
		}
	}


	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderTick(RenderTickEvent event) {
		EmoteHandler.onRenderTick(Minecraft.getInstance());
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@OnlyIn(Dist.CLIENT)
	public void preRenderLiving(RenderLivingEvent.Pre<Player, ?> event) {
		if(event.getEntity() instanceof Player player)
			EmoteHandler.preRender(event.getPoseStack(), player);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	@OnlyIn(Dist.CLIENT)
	public void postRenderLiving(RenderLivingEvent.Post<Player, ?> event) {
		if(event.getEntity() instanceof Player player)
			EmoteHandler.postRender(event.getPoseStack(), player);
	}


}
