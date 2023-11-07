package vazkii.zeta;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.block.ext.BlockExtensionFactory;
import vazkii.zeta.capability.ZetaCapabilityManager;
import vazkii.zeta.item.ext.ItemExtensionFactory;
import vazkii.zeta.registry.BrewingRegistry;
import vazkii.zeta.config.IZetaConfigInternals;
import vazkii.zeta.config.SectionDefinition;
import vazkii.zeta.config.ConfigManager;
import vazkii.zeta.event.bus.IZetaLoadEvent;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.ZetaEventBus;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ModuleFinder;
import vazkii.zeta.module.ZetaCategory;
import vazkii.zeta.module.ZetaModuleManager;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.CraftingExtensionsRegistry;
import vazkii.zeta.registry.DyeablesRegistry;
import vazkii.zeta.registry.RenderLayerRegistry;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;

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
