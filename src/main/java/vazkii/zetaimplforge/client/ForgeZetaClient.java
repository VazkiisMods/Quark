package vazkii.zetaimplforge.client;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.zeta.Zeta;
import vazkii.zeta.client.ZetaClient;
import vazkii.zeta.event.client.ZFirstClientTick;
import vazkii.zeta.event.client.ZRegisterReloadListeners;
import vazkii.zetaimplforge.event.client.ForgeZAddItemColorHandlers;
import vazkii.zetaimplforge.event.client.ForgeZAddModelLayers;
import vazkii.zetaimplforge.event.client.ForgeZAddModels;
import vazkii.zetaimplforge.event.client.ForgeZClientSetup;
import vazkii.zetaimplforge.event.client.ForgeZHighlightBlock;
import vazkii.zetaimplforge.event.client.ForgeZKeyMapping;
import vazkii.zetaimplforge.event.client.ForgeZModelBakingCompleted;
import vazkii.zetaimplforge.event.client.ForgeZPreTextureStitch;
import vazkii.zetaimplforge.event.client.ForgeZRenderChat;
import vazkii.zetaimplforge.event.client.ForgeZRenderCrosshair;
import vazkii.zetaimplforge.event.client.ForgeZTooltipComponents;

public class ForgeZetaClient extends ZetaClient {
	public ForgeZetaClient(Zeta z) {
		super(z);
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

		MinecraftForge.EVENT_BUS.addListener(this::renderTick);
		MinecraftForge.EVENT_BUS.addListener(this::clientTick);
		MinecraftForge.EVENT_BUS.addListener(this::renderBlockHighlight);
		MinecraftForge.EVENT_BUS.addListener(this::renderGameOverlay);
		MinecraftForge.EVENT_BUS.addListener(this::renderGameOverlayPre);
		MinecraftForge.EVENT_BUS.addListener(this::renderGameOverlayPost);
	}

	public void registerBlockColors(RegisterColorHandlersEvent.Block evt) {
		z.registry.submitBlockColors(evt::register);
	}

	public void registerItemColors(RegisterColorHandlersEvent.Item event) {
		loadBus.fire(new ForgeZAddItemColorHandlers(event));
		z.registry.submitItemColors(event::register);
	}

	public void clientSetup(FMLClientSetupEvent event) {
		loadBus.fire(new ForgeZClientSetup(event));
	}

	public void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		loadBus.fire(new ZRegisterReloadListeners(event::registerReloadListener));
	}

	public void modelBake(ModelEvent.BakingCompleted event) {
		loadBus.fire(new ForgeZModelBakingCompleted(event));
	}

	public void modelLayers(EntityRenderersEvent.AddLayers event) {
		loadBus.fire(new ForgeZAddModelLayers(event));
	}

	public void textureStitch(TextureStitchEvent.Pre event) {
		loadBus.fire(new ForgeZPreTextureStitch(event));
	}

	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		loadBus.fire(new ForgeZKeyMapping(event));
	}

	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		loadBus.fire(new ForgeZAddModels(event));
	}

	public void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		loadBus.fire(new ForgeZTooltipComponents(event));
	}

	public void renderTick(TickEvent.RenderTickEvent e) {
		if(e.phase == TickEvent.Phase.START)
			ticker.startRenderTick(e.renderTickTime);
		else
			ticker.endRenderTick();
	}

	boolean clientTicked = false;
	public void clientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			ticker.endClientTick();

			if(!clientTicked) {
				loadBus.fire(new ZFirstClientTick());
				clientTicked = true;
			}
		}
	}

	public void renderBlockHighlight(RenderHighlightEvent.Block e) {
		playBus.fire(new ForgeZHighlightBlock(e));
	}

	//TODO: This probably should have been a PRE/POST event (just copying quark here)
	public void renderGameOverlay(RenderGuiOverlayEvent e) {
		if(e.getOverlay() == VanillaGuiOverlay.CROSSHAIR.type())
			playBus.fire(new ForgeZRenderCrosshair(e));
	}

	public void renderGameOverlayPre(RenderGuiOverlayEvent.Pre e) {
		if(e.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			playBus.fire(new ForgeZRenderChat.Pre(e));
	}

	public void renderGameOverlayPost(RenderGuiOverlayEvent.Post e) {
		if(e.getOverlay() == VanillaGuiOverlay.CHAT_PANEL.type())
			playBus.fire(new ForgeZRenderChat.Post(e));
	}
}
