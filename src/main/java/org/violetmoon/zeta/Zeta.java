package org.violetmoon.zeta;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.block.ext.BlockExtensionFactory;
import org.violetmoon.zeta.capability.ZetaCapabilityManager;
import org.violetmoon.zeta.config.ConfigManager;
import org.violetmoon.zeta.config.IZetaConfigInternals;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.event.bus.*;
import org.violetmoon.zeta.item.ext.ItemExtensionFactory;
import org.violetmoon.zeta.module.ModuleFinder;
import org.violetmoon.zeta.module.ZetaCategory;
import org.violetmoon.zeta.module.ZetaModuleManager;
import org.violetmoon.zeta.network.ZetaNetworkHandler;
import org.violetmoon.zeta.registry.*;
import org.violetmoon.zeta.util.ZetaSide;

/**
 * do not touch forge OR quark from this package, it will later be split off
 */
public abstract class Zeta {
	public static final Logger GLOBAL_LOG = LogManager.getLogger("zeta");

	public Zeta(String modid, Logger log, ZetaSide side) {
		this.log = log;

		this.modid = modid;
		this.side = side;
		this.loadBus = new ZetaEventBus<>(LoadEvent.class, IZetaLoadEvent.class, log);
		this.playBus = new ZetaEventBus<>(PlayEvent.class, IZetaPlayEvent.class, null);

		this.modules = createModuleManager();
		this.registry = createRegistry();
		this.renderLayerRegistry = createRenderLayerRegistry();
		this.dyeables = createDyeablesRegistry();
		this.craftingExtensions = createCraftingExtensionsRegistry();
		this.brewingRegistry = createBrewingRegistry();

		this.blockExtensions = createBlockExtensionFactory();
		this.itemExtensions = createItemExtensionFactory();
		this.capabilityManager = createCapabilityManager();

		loadBus.subscribe(craftingExtensions)
			.subscribe(brewingRegistry);
	}

	public final Logger log;

	public final String modid;
	public final ZetaSide side;
	public final ZetaEventBus<IZetaLoadEvent> loadBus;
	public final ZetaEventBus<IZetaPlayEvent> playBus;
	public final ZetaModuleManager modules;

	public final ZetaRegistry registry;
	public final RenderLayerRegistry renderLayerRegistry;
	public final DyeablesRegistry dyeables;
	public final CraftingExtensionsRegistry craftingExtensions;
	public final BrewingRegistry brewingRegistry;

	public final ZetaCapabilityManager capabilityManager;
	public final BlockExtensionFactory blockExtensions;
	public final ItemExtensionFactory itemExtensions;

	public ConfigManager configManager; //This could do with being split up into various pieces?
	public IZetaConfigInternals configInternals;

	public void loadModules(Iterable<ZetaCategory> categories, ModuleFinder finder, Object rootPojo) {
		modules.initCategories(categories);
		modules.load(finder);

		//The reason why there's a circular dependency between configManager and configInternals:
		// - ConfigManager determines the shape and layout of the config file
		// - The platform-specific configInternals loads the actual values, from the platform-specfic config file
		// - Only then can ConfigManager do the initial config load

		this.configManager = new ConfigManager(this, rootPojo);
		this.configInternals = makeConfigInternals(configManager.getRootConfig());
		this.configManager.onReload();
	}

	// modloader services
	public abstract boolean isModLoaded(String modid);
	public abstract @Nullable String getModDisplayName(String modid);

	// config
	public abstract IZetaConfigInternals makeConfigInternals(SectionDefinition rootSection);

	// general xplat stuff
	public ZetaModuleManager createModuleManager() {
		return new ZetaModuleManager(this);
	}
	public abstract ZetaRegistry createRegistry();
	public RenderLayerRegistry createRenderLayerRegistry() {
		return new RenderLayerRegistry();
	}
	public abstract CraftingExtensionsRegistry createCraftingExtensionsRegistry();
	public DyeablesRegistry createDyeablesRegistry() {
		return new DyeablesRegistry(this);
	}
	public abstract BrewingRegistry createBrewingRegistry();
	public abstract ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion);
	public abstract ZetaCapabilityManager createCapabilityManager();
	public BlockExtensionFactory createBlockExtensionFactory() {
		return BlockExtensionFactory.DEFAULT;
	}
	public abstract ItemExtensionFactory createItemExtensionFactory();

	// misc "ah fuck i need to interact with the modloader" stuff
	public abstract boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr);

	// Let's Jump
	public abstract void start();
}
