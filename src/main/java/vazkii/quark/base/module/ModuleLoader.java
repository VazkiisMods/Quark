package vazkii.quark.base.module;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.config.ConfigResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class ModuleLoader {

	private enum Step {
		CONSTRUCT, CONSTRUCT_CLIENT, REGISTER, POST_REGISTER, CONFIG_CHANGED, CONFIG_CHANGED_CLIENT, SETUP, SETUP_CLIENT,
		REGISTER_RELOADABLE, MODEL_BAKE, MODEL_LAYERS, TEXTURE_STITCH, POST_TEXTURE_STITCH, LOAD_COMPLETE, GENERATE_HINTS,
		FIRST_CLIENT_TICK, REGISTER_KEYBINDS, REGISTER_ADDITIONAL_MODELS, REGISTER_TOOLTIP_COMPONENT_FACTORIES,
		REGISTER_ITEM_COLORS, REGISTER_BLOCK_COLORS
	}

	public static final ModuleLoader INSTANCE = new ModuleLoader();

	private Map<Class<? extends QuarkModule>, QuarkModule> foundModules = new HashMap<>();
	private final List<Step> stepsHandled = new ArrayList<>();

	private ConfigResolver config;
	private Runnable onConfigReloadJEI;
	private boolean clientTicked = false;

	private ModuleLoader() { }

	public void start() {
		findModules();
		dispatch(Step.CONSTRUCT, QuarkModule::construct);
		resolveConfigSpec();
	}

	@OnlyIn(Dist.CLIENT)
	public void clientStart() {
		dispatch(Step.CONSTRUCT_CLIENT, QuarkModule::constructClient);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void findModules() {
		ModuleFinder finder = new ModuleFinder();
		finder.findModules();
		foundModules = finder.getFoundModules();
	}

	private void resolveConfigSpec() {
		config = new ConfigResolver();
		config.makeSpec();
	}

	public ModConfig getConfig() {
		return config.getConfig();
	}

	public void register() {
		dispatch(Step.REGISTER, QuarkModule::register);
		dispatch(Step.POST_REGISTER, QuarkModule::postRegister);
		CreativeTabHandler.finalizeTabs();
		config.registerConfigBoundElements();
	}

	public void configChanged() {
		if(!stepsHandled.contains(Step.POST_REGISTER))
			return; // We don't want to mess with changing config values before objects are registered

		if (onConfigReloadJEI != null)
			onConfigReloadJEI.run();
		config.configChanged();
		dispatch(Step.CONFIG_CHANGED, QuarkModule::configChanged);
	}

	@OnlyIn(Dist.CLIENT)
	public void configChangedClient() {
		if(!stepsHandled.contains(Step.POST_REGISTER))
			return; // We don't want to mess with changing config values before objects are registered

		dispatch(Step.CONFIG_CHANGED_CLIENT, QuarkModule::configChangedClient);
	}

	public void setup(ParallelDispatchEvent event) {
		Quark.proxy.handleQuarkConfigChange();
		dispatch(Step.SETUP, m -> m.setup(event::enqueueWork));
	}

	@OnlyIn(Dist.CLIENT)
	public void clientSetup(ParallelDispatchEvent event) {
		dispatch(Step.SETUP_CLIENT, m -> m.clientSetup(event::enqueueWork));
	}

	@OnlyIn(Dist.CLIENT)
	public void registerReloadListeners(RegisterClientReloadListenersEvent event) {
		dispatch(Step.SETUP_CLIENT, m -> m.registerReloadListeners(event::registerReloadListener));
	}

	@OnlyIn(Dist.CLIENT)
	public void modelBake(BakingCompleted event) {
		dispatch(Step.MODEL_BAKE, m -> m.modelBake(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void modelLayers(EntityRenderersEvent.AddLayers event) {
		dispatch(Step.MODEL_LAYERS, m -> m.modelLayers(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void textureStitch(TextureStitchEvent.Pre event) {
		dispatch(Step.TEXTURE_STITCH, m -> m.textureStitch(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void postTextureStitch(TextureStitchEvent.Post event) {
		dispatch(Step.POST_TEXTURE_STITCH, m -> m.postTextureStitch(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		dispatch(Step.REGISTER_KEYBINDS, m -> m.registerKeybinds(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		dispatch(Step.REGISTER_ADDITIONAL_MODELS, m -> m.registerAdditionalModels(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		dispatch(Step.REGISTER_TOOLTIP_COMPONENT_FACTORIES, m -> m.registerClientTooltipComponentFactories(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void registerItemColors(RegisterColorHandlersEvent.Item event) {
		dispatch(Step.REGISTER_ITEM_COLORS, m -> m.registerItemColors(event));
	}

	@OnlyIn(Dist.CLIENT)
	public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		dispatch(Step.REGISTER_BLOCK_COLORS, m -> m.registerBlockColors(event));
	}

	public void loadComplete(ParallelDispatchEvent event) {
		dispatch(Step.LOAD_COMPLETE, m -> m.loadComplete(event::enqueueWork));
	}

	public void addStackInfo(BiConsumer<Item, Component> consumer) {
		dispatch(Step.GENERATE_HINTS, m -> {
			if(m.enabled)
				m.addStackInfo(consumer);
		});
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void firstClientTick(ClientTickEvent event) {
		if(!clientTicked && event.phase == Phase.END) {
			dispatch(Step.FIRST_CLIENT_TICK, QuarkModule::firstClientTick);
			clientTicked = true;
		}
	}

	private void dispatch(Step step, Consumer<QuarkModule> run) {
		Quark.LOG.info("Dispatching Module Step " + step);
		foundModules.values().forEach(run);
		stepsHandled.add(step);
	}

	public boolean isModuleEnabled(Class<? extends QuarkModule> moduleClazz) {
		QuarkModule module = getModuleInstance(moduleClazz);
		return module != null && module.enabled;
	}

	public boolean isModuleEnabledOrOverlapping(Class<? extends QuarkModule> moduleClazz) {
		QuarkModule module = getModuleInstance(moduleClazz);
		return module != null && (module.enabled || module.disabledByOverlap);
	}

	public QuarkModule getModuleInstance(Class<? extends QuarkModule> moduleClazz) {
		return foundModules.get(moduleClazz);
	}

	public boolean isItemEnabled(Item i) {
		if(i instanceof IQuarkItem qi) {
			return qi.isEnabled();
		}
		else if(i instanceof BlockItem bi) {
			Block b = bi.getBlock();
			if(b instanceof IQuarkBlock qb) {
				return qb.isEnabled();
			}
		}

		return true;
	}

	/**
	 * Meant only to be called internally.
	 */
	public void initJEICompat(Runnable jeiRunnable) {
		onConfigReloadJEI = jeiRunnable;
		onConfigReloadJEI.run();
	}

}
