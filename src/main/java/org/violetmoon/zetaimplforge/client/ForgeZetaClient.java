package org.violetmoon.zetaimplforge.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.client.ClientRegistryExtension;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.event.load.*;
import org.violetmoon.zeta.client.event.play.*;
import org.violetmoon.zetaimplforge.client.event.load.*;
import org.violetmoon.zetaimplforge.client.event.player.*;
import org.violetmoon.zetaimplforge.mixin.client.AccessorBlockColors;
import org.violetmoon.zetaimplforge.mixin.client.AccessorItemColors;

public class ForgeZetaClient extends ZetaClient {
	public ForgeZetaClient(Zeta z) {
		super(z);
	}

	@Override
	public @Nullable BlockColor getBlockColor(BlockColors bcs, Block block) {
		return ForgeRegistries.BLOCKS.getDelegate(block)
			.map(ref -> ((AccessorBlockColors) bcs).zeta$getBlockColors().get(ref))
			.orElse(null);
	}

	@Override
	public @Nullable ItemColor getItemColor(ItemColors ics, ItemLike itemlike) {
		return ForgeRegistries.ITEMS.getDelegate(itemlike.asItem())
			.map(ref -> ((AccessorItemColors) ics).zeta$getItemColors().get(ref))
			.orElse(null);
	}

	@Override
	public ClientRegistryExtension createClientRegistryExtension() {
		return new ForgeClientRegistryExtension(zeta);
	}

	@Override
	public void start() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		bus.addListener(this::registerBlockColors);
		bus.addListener(this::registerItemColors);
		bus.addListener(this::clientSetup);
		bus.addListener(this::registerReloadListeners);
		bus.addListener(this::modelBake);
		bus.addListener(this::modelLayers);
		bus.addListener(this::textureStitch);
		bus.addListener(this::registerKeybinds);
		bus.addListener(this::registerAdditionalModels);
		bus.addListener(this::registerClientTooltipComponentFactories);
		bus.addListener(this::registerLayerDefinitions);

		MinecraftForge.EVENT_BUS.addListener(this::renderTick);
		MinecraftForge.EVENT_BUS.addListener(this::clientTick);
		MinecraftForge.EVENT_BUS.addListener(this::inputMouseButton);
		MinecraftForge.EVENT_BUS.addListener(this::inputKey);
		MinecraftForge.EVENT_BUS.addListener(this::screenshot);
		MinecraftForge.EVENT_BUS.addListener(this::movementInputUpdate);
		MinecraftForge.EVENT_BUS.addListener(this::renderBlockHighlight);
		MinecraftForge.EVENT_BUS.addListener(this::gatherTooltipComponents);

		MinecraftForge.EVENT_BUS.addListener(this::customizeF3);

		MinecraftForge.EVENT_BUS.addListener(this::renderContainerScreenForeground);
		MinecraftForge.EVENT_BUS.addListener(this::renderContainerScreenBackground);

		MinecraftForge.EVENT_BUS.addListener(this::renderGameOverlayNeitherPreNorPost);
		MinecraftForge.EVENT_BUS.addListener(this::renderGuiOverlayPre);
		MinecraftForge.EVENT_BUS.addListener(this::renderGuiOverlayPost);
		MinecraftForge.EVENT_BUS.addListener(this::renderPlayerPre);
		MinecraftForge.EVENT_BUS.addListener(this::renderPlayerPost);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::renderLivingPreHighest);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::renderLivingPostLowest);
		MinecraftForge.EVENT_BUS.addListener(this::renderTooltipGatherComponents);
		MinecraftForge.EVENT_BUS.addListener(this::renderTooltipGatherComponentsLow);

		MinecraftForge.EVENT_BUS.addListener(this::screenInitPre);
		MinecraftForge.EVENT_BUS.addListener(this::screenInitPost);
		MinecraftForge.EVENT_BUS.addListener(this::screenRenderPre);
		MinecraftForge.EVENT_BUS.addListener(this::screenRenderPost);
		MinecraftForge.EVENT_BUS.addListener(this::screenMouseButtonPressedPre);
		MinecraftForge.EVENT_BUS.addListener(this::screenMouseButtonPressedPost);
		MinecraftForge.EVENT_BUS.addListener(this::screenMouseScrolledPre);
		MinecraftForge.EVENT_BUS.addListener(this::screenMouseScrolledPost);
		MinecraftForge.EVENT_BUS.addListener(this::screenKeyPressedPre);
		MinecraftForge.EVENT_BUS.addListener(this::screenKeyPressedPost);
		MinecraftForge.EVENT_BUS.addListener(this::screenCharacterTypedPre);
		MinecraftForge.EVENT_BUS.addListener(this::screenCharacterTypedPost);
		MinecraftForge.EVENT_BUS.addListener(this::screenOpening);
	}

	public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		ZAddBlockColorHandlers e = loadBus.fire(new ForgeZAddBlockColorHandlers(event), ZAddBlockColorHandlers.class);
		loadBus.fire(e.makePostEvent(), ZAddBlockColorHandlers.Post.class);
	}

	public void registerItemColors(RegisterColorHandlersEvent.Item event) {
		ZAddItemColorHandlers e = loadBus.fire(new ForgeZAddItemColorHandlers(event), ZAddItemColorHandlers.class);
		loadBus.fire(e.makePostEvent(), ZAddItemColorHandlers.Post.class);
	}

	public void clientSetup(FMLClientSetupEvent event) {
		loadBus.fire(new ForgeZClientSetup(event), ZClientSetup.class);
	}

	public void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		loadBus.fire(new ZRegisterReloadListeners(event::registerReloadListener), ZRegisterReloadListeners.class);
	}

	public void modelBake(ModelEvent.BakingCompleted event) {
		loadBus.fire(new ForgeZModelBakingCompleted(event), ZModelBakingCompleted.class);
	}

	public void modelLayers(EntityRenderersEvent.AddLayers event) {
		loadBus.fire(new ForgeZAddModelLayers(event), ZAddModelLayers.class);
	}

	public void textureStitch(TextureStitchEvent.Pre event) {
		loadBus.fire(new ForgeZPreTextureStitch(event), ZPreTextureStitch.class);
	}

	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		loadBus.fire(new ForgeZKeyMapping(event), ZKeyMapping.class);
	}

	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		loadBus.fire(new ForgeZAddModels(event), ZAddModels.class);
	}

	public void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		loadBus.fire(new ForgeZTooltipComponents(event), ZTooltipComponents.class);
	}

	public void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) {
		loadBus.fire(new ForgeZRegisterLayerDefinitions(e), ZRegisterLayerDefinitions.class);
	}

	//TODO: move ticker stuff out of forge event handlers, subscribe to them from zeta
	// Also these events are a mess lol; sometimes there's 2 start/end events, sometimes there's
	// one event with multiple Phases... bad
	public void renderTick(TickEvent.RenderTickEvent e) {
		playBus.fire(new ForgeZRenderTick(e), ZRenderTick.class);
	}

	boolean clientTicked = false;
	public void clientTick(TickEvent.ClientTickEvent event) {
		if(!clientTicked) {
			loadBus.fire(new ZFirstClientTick());
			clientTicked = true;
		}

		if(event.phase == TickEvent.Phase.START)
			playBus.fire(new ZStartClientTick());
		else
			playBus.fire(new ZEndClientTick());
	}

	public void inputMouseButton(InputEvent.MouseButton e) {
		playBus.fire(new ForgeZInput.MouseButton(e), ZInput.MouseButton.class);
	}

	public void inputKey(InputEvent.Key e) {
		playBus.fire(new ForgeZInput.Key(e), ZInput.Key.class);
	}

	public void screenshot(ScreenshotEvent e) {
		playBus.fire(new ZScreenshot());
	}

	public void movementInputUpdate(MovementInputUpdateEvent e) {
		playBus.fire(new ForgeZInputUpdate(e), ZInputUpdate.class);
	}

	public void renderBlockHighlight(RenderHighlightEvent.Block e) {
		playBus.fire(new ForgeZHighlightBlock(e), ZHighlightBlock.class);
	}

	public void gatherTooltipComponents(RenderTooltipEvent.GatherComponents e) {
		playBus.fire(new ForgeZGatherTooltipComponents(e), ZGatherTooltipComponents.class);
	}

	public void customizeF3(CustomizeGuiOverlayEvent.DebugText e) {
		playBus.fire(new ForgeZCustomizeDebugText(e), ZCustomizeDebugText.class);
	}

	public void renderContainerScreenForeground(ContainerScreenEvent.Render.Foreground e) {
		playBus.fire(new ForgeZRenderContainerScreen.Foreground(e), ZRenderContainerScreen.Foreground.class);
	}

	public void renderContainerScreenBackground(ContainerScreenEvent.Render.Background e) {
		playBus.fire(new ForgeZRenderContainerScreen.Background(e), ZRenderContainerScreen.Background.class);
	}

	//TODO: This probably should have been a PRE/POST event (just copying quark here)
	public void renderGameOverlayNeitherPreNorPost(RenderGuiOverlayEvent e) {
		if(e.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair(e), ZRenderGuiOverlay.Crosshair.class);
		else if(e.getOverlay() == VanillaGuiOverlay.HOTBAR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar(e), ZRenderGuiOverlay.Hotbar.class);
	}

	public void renderGuiOverlayPre(RenderGuiOverlayEvent.Pre e) {
		if (e.getOverlay() == VanillaGuiOverlay.HOTBAR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Pre(e), ZRenderGuiOverlay.Hotbar.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Pre(e), ZRenderGuiOverlay.Crosshair.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Pre(e), ZRenderGuiOverlay.PlayerHealth.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Pre(e), ZRenderGuiOverlay.ArmorLevel.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.DEBUG_TEXT.type())
			playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Pre(e), ZRenderGuiOverlay.DebugText.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.POTION_ICONS.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Pre(e), ZRenderGuiOverlay.PotionIcons.Pre.class);
		else if (e.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Pre(e), ZRenderGuiOverlay.ChatPanel.Pre.class);
	}

	public void renderGuiOverlayPost(RenderGuiOverlayEvent.Post e) {
		if (e.getOverlay() == VanillaGuiOverlay.HOTBAR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Hotbar.Post(e), ZRenderGuiOverlay.Hotbar.Post.class);
		else if (e.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type())
			playBus.fire(new ForgeZRenderGuiOverlay.Crosshair.Post(e), ZRenderGuiOverlay.Crosshair.Post.class);
		else if (e.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PlayerHealth.Post(e), ZRenderGuiOverlay.PlayerHealth.Post.class);
		else if (e.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ArmorLevel.Post(e), ZRenderGuiOverlay.ArmorLevel.Post.class);
		else if (e.getOverlay() == VanillaGuiOverlay.DEBUG_TEXT.type())
			playBus.fire(new ForgeZRenderGuiOverlay.DebugText.Post(e), ZRenderGuiOverlay.DebugText.Post.class);
		else if (e.getOverlay() == VanillaGuiOverlay.POTION_ICONS.type())
			playBus.fire(new ForgeZRenderGuiOverlay.PotionIcons.Post(e), ZRenderGuiOverlay.PotionIcons.Post.class);
		else if (e.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			playBus.fire(new ForgeZRenderGuiOverlay.ChatPanel.Post(e), ZRenderGuiOverlay.ChatPanel.Post.class);
	}

	public void renderPlayerPre(RenderPlayerEvent.Pre e) {
		playBus.fire(new ForgeZRenderPlayer.Pre(e), ZRenderPlayer.Pre.class);
	}

	public void renderPlayerPost(RenderPlayerEvent.Post e) {
		playBus.fire(new ForgeZRenderPlayer.Post(e), ZRenderPlayer.Post.class);
	}

	public void renderLivingPreHighest(RenderLivingEvent.Pre<?, ?> e) {
		playBus.fire(new ForgeZRenderLiving.PreHighest(e), ZRenderLiving.PreHighest.class);
	}

	public void renderLivingPostLowest(RenderLivingEvent.Post<?, ?> e) {
		playBus.fire(new ForgeZRenderLiving.PostLowest(e), ZRenderLiving.PostLowest.class);
	}

	public void renderTooltipGatherComponents(RenderTooltipEvent.GatherComponents e) {
		playBus.fire(new ForgeZRenderTooltip.GatherComponents(e), ZRenderTooltip.GatherComponents.class);
	}

	public void renderTooltipGatherComponentsLow(RenderTooltipEvent.GatherComponents e) {
		playBus.fire(new ForgeZRenderTooltip.GatherComponents.Low(e), ZRenderTooltip.GatherComponents.Low.class);
	}

	public void screenInitPre(ScreenEvent.Init.Pre e) {
		playBus.fire(new ForgeZScreen.Init.Pre(e), ZScreen.Init.Pre.class);
	}

	public void screenInitPost(ScreenEvent.Init.Post e) {
		playBus.fire(new ForgeZScreen.Init.Post(e), ZScreen.Init.Post.class);
	}

	public void screenRenderPre(ScreenEvent.Render.Pre e) {
		playBus.fire(new ForgeZScreen.Render.Pre(e), ZScreen.Render.Pre.class);
	}

	public void screenRenderPost(ScreenEvent.Render.Post e) {
		playBus.fire(new ForgeZScreen.Render.Post(e), ZScreen.Render.Post.class);
	}

	public void screenMouseButtonPressedPre(ScreenEvent.MouseButtonPressed.Pre e) {
		playBus.fire(new ForgeZScreen.MouseButtonPressed.Pre(e), ZScreen.MouseButtonPressed.Pre.class);
	}

	public void screenMouseButtonPressedPost(ScreenEvent.MouseButtonPressed.Post e) {
		playBus.fire(new ForgeZScreen.MouseButtonPressed.Post(e), ZScreen.MouseButtonPressed.Post.class);
	}

	public void screenMouseScrolledPre(ScreenEvent.MouseScrolled.Pre e) {
		playBus.fire(new ForgeZScreen.MouseScrolled.Pre(e), ZScreen.MouseScrolled.Pre.class);
	}

	public void screenMouseScrolledPost(ScreenEvent.MouseScrolled.Post e) {
		playBus.fire(new ForgeZScreen.MouseScrolled.Post(e), ZScreen.MouseScrolled.Post.class);
	}

	public void screenKeyPressedPre(ScreenEvent.KeyPressed.Pre e) {
		playBus.fire(new ForgeZScreen.KeyPressed.Pre(e), ZScreen.KeyPressed.Pre.class);
	}

	public void screenKeyPressedPost(ScreenEvent.KeyPressed.Post e) {
		playBus.fire(new ForgeZScreen.KeyPressed.Post(e), ZScreen.KeyPressed.Post.class);
	}

	public void screenCharacterTypedPre(ScreenEvent.CharacterTyped.Pre e) {
		playBus.fire(new ForgeZScreen.CharacterTyped.Pre(e), ZScreen.CharacterTyped.Pre.class);
	}

	public void screenCharacterTypedPost(ScreenEvent.CharacterTyped.Post e) {
		playBus.fire(new ForgeZScreen.CharacterTyped.Post(e), ZScreen.CharacterTyped.Post.class);
	}

	public void screenOpening(ScreenEvent.Opening e) {
		playBus.fire(new ForgeZScreen.Opening(e), ZScreen.Opening.class);
	}
}
