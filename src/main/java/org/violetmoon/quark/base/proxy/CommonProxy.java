package org.violetmoon.quark.base.proxy;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.violetmoon.quark.QuarkForgeCapabilities;
import org.violetmoon.quark.api.ICustomSorting;
import org.violetmoon.quark.api.IMagnetTracker;
import org.violetmoon.quark.api.IPistonCallback;
import org.violetmoon.quark.api.IRuneColorProvider;
import org.violetmoon.quark.api.ITransferManager;
import org.violetmoon.quark.api.QuarkCapabilities;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.capability.CapabilityHandler;
import org.violetmoon.quark.base.config.SyncedFlagHandler;
import org.violetmoon.quark.base.handler.ContributorRewardHandler;
import org.violetmoon.quark.base.handler.CreativeTabHandler;
import org.violetmoon.quark.base.handler.EntityAttributeHandler;
import org.violetmoon.quark.base.handler.FuelHandler;
import org.violetmoon.quark.base.handler.GeneralConfig;
import org.violetmoon.quark.base.handler.QuarkSounds;
import org.violetmoon.quark.base.handler.RecipeCrawlHandler;
import org.violetmoon.quark.base.handler.ToolInteractionHandler;
import org.violetmoon.quark.base.handler.UndergroundBiomeHandler;
import org.violetmoon.quark.base.handler.WoodSetHandler;
import org.violetmoon.quark.base.handler.advancement.QuarkAdvancementHandler;
import org.violetmoon.quark.base.network.QuarkNetwork;
import org.violetmoon.quark.base.recipe.ExclusionRecipe;
import org.violetmoon.quark.base.world.EntitySpawnHandler;
import org.violetmoon.quark.base.world.WorldGenHandler;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaCategory;
import org.violetmoon.zetaimplforge.module.ModFileScanDataModuleFinder;

import net.minecraft.core.Registry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonProxy {

	public static boolean jingleTheBells = false;

	public void start() {
		//todo put this bit in forge-specific code
		Quark.ZETA.capabilityManager
			.register(QuarkCapabilities.SORTING, QuarkForgeCapabilities.SORTING)
			.register(QuarkCapabilities.TRANSFER, QuarkForgeCapabilities.TRANSFER)
			.register(QuarkCapabilities.PISTON_CALLBACK, QuarkForgeCapabilities.PISTON_CALLBACK)
			.register(QuarkCapabilities.MAGNET_TRACKER_CAPABILITY, QuarkForgeCapabilities.MAGNET_TRACKER_CAPABILITY)
			.register(QuarkCapabilities.RUNE_COLOR, QuarkForgeCapabilities.RUNE_COLOR);
		//weird forge capability-implementation-class stuff
		MinecraftForge.EVENT_BUS.addListener((RegisterCapabilitiesEvent e) -> {
			e.register(ICustomSorting.class);
			e.register(ITransferManager.class);
			e.register(IPistonCallback.class);
			e.register(IMagnetTracker.class);
			e.register(IRuneColorProvider.class);
		});

		Quark.ZETA.loadBus
			.subscribe(EntityAttributeHandler.class)
			.subscribe(ContributorRewardHandler.class)
			.subscribe(CreativeTabHandler.class)
			.subscribe(QuarkNetwork.class)
			.subscribe(QuarkSounds.class)
			.subscribe(QuarkAdvancementHandler.class)
			.subscribe(RecipeCrawlHandler.class)
			.subscribe(ToolInteractionHandler.class)
			.subscribe(WoodSetHandler.class)
			.subscribe(WorldGenHandler.class)
			.subscribe(FuelHandler.class)
			.subscribe(UndergroundBiomeHandler.class)
			.subscribe(EntitySpawnHandler.class)
			.subscribe(this);

		Quark.ZETA.playBus
			.subscribe(CapabilityHandler.class)
			.subscribe(SyncedFlagHandler.class)
			.subscribe(ContributorRewardHandler.class)
			.subscribe(FuelHandler.class)
			.subscribe(ToolInteractionHandler.class);

		MinecraftForge.EVENT_BUS.register(ToolInteractionHandler.class);

		Quark.ZETA.loadModules(
			List.of(
				new ZetaCategory("automation", Items.REDSTONE),
				new ZetaCategory("building", Items.BRICKS),
				new ZetaCategory("management", Items.CHEST),
				new ZetaCategory("tools", Items.IRON_PICKAXE),
				new ZetaCategory("tweaks", Items.NAUTILUS_SHELL),
				new ZetaCategory("world", Items.GRASS_BLOCK),
				new ZetaCategory("mobs", Items.PIG_SPAWN_EGG),
				new ZetaCategory("client", Items.ENDER_EYE),
				new ZetaCategory("experimental", Items.TNT),
				new ZetaCategory("oddities", Items.CHORUS_FRUIT, Quark.ODDITIES_ID)
			),
			new ModFileScanDataModuleFinder(Quark.MOD_ID),
			GeneralConfig.INSTANCE
		);

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::configChanged);
		WorldGenHandler.registerBiomeModifier(bus);

		LocalDateTime now = LocalDateTime.now();
		if (now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 16 || now.getMonth() == Month.JANUARY && now.getDayOfMonth() <= 2)
			jingleTheBells = true;
	}

	@LoadEvent
	public void setup(ZCommonSetup event) {
		handleQuarkConfigChange();
	}

	//TODO find a better place for this little one-off thing, lol
	@LoadEvent
	public void recipe(ZRegister event) {
		event.getRegistry().register(ExclusionRecipe.SERIALIZER, "exclusion", Registry.RECIPE_SERIALIZER_REGISTRY);
	}

	//forge event
	public void configChanged(ModConfigEvent event) {
		if(!event.getConfig().getModId().equals(Quark.MOD_ID) || Quark.ZETA.configInternals == null)
			return;

		// https://github.com/VazkiiMods/Quark/commit/b0e00864f74539d8650cb349e88d0302a0fda8e4
		// "The Forge config api writes to the config file on every single change
		//  to the config, which would cause the file watcher to trigger
		//  a config reload while the config gui is committing changes."
		if(System.currentTimeMillis() - Quark.ZETA.configInternals.debounceTime() > 20)
			handleQuarkConfigChange();
	}

	//TODO: probably find a better spot for this? It's not *only* fired when the
	// config file is externally changed, but also when it's changed through config GUI,
	// which means we roundtrip through the on-disk representation for no good reason
	public void handleQuarkConfigChange() {
		Quark.ZETA.configManager.onReload();
		Quark.ZETA.loadBus.fire(new ZConfigChanged());
	}

	/**
	 * Use an item WITHOUT sending the use to the server. This will cause ghost interactions if used incorrectly!
	 */
	public InteractionResult clientUseItem(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.PASS;
	}

	public boolean isClientPlayerHoldingShift() {
		return false;
	}
}
