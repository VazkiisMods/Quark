package vazkii.quark.base.proxy;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.Quark;
import vazkii.quark.base.capability.CapabilityHandler;
import vazkii.quark.base.handler.*;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.config.IConfigCallback;
import vazkii.quark.base.module.sync.SyncedFlagHandler;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.recipe.*;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.zeta.module.ZetaCategory;
import vazkii.zeta.module.ZetaModuleManager;
import vazkii.zetaimplforge.module.ModFileScanDataModuleFinder;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

public class CommonProxy {

	private int lastConfigChange = -11;
	public static boolean jingleTheBells = false;
	private boolean configGuiSaving = false;

	public void start() {
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":exclusion", ExclusionRecipe.SERIALIZER);
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":maintaining", DataMaintainingRecipe.SERIALIZER);
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":maintaining_smelting", DataMaintainingSmeltingRecipe.SERIALIZER);
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":maintaining_campfire", DataMaintainingCampfireRecipe.SERIALIZER);
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":maintaining_smoking", DataMaintainingSmokingRecipe.SERIALIZER);

		QuarkSounds.start();

		//TODO: maybe find a better place for Zeta module init
		ZetaModuleManager modules = Quark.ZETA.modules;
		modules.initCategories(List.of(
			new ZetaCategory("automation", Items.REDSTONE),
			new ZetaCategory("building", Items.BRICKS),
			new ZetaCategory("management", Items.CHEST),
			new ZetaCategory("tools", Items.IRON_PICKAXE),
			new ZetaCategory("tweaks", Items.NAUTILUS_SHELL),
			new ZetaCategory("world", Items.GRASS_BLOCK),
			new ZetaCategory("mobs", Items.PIG_SPAWN_EGG),
			new ZetaCategory("client", Items.ENDER_EYE),
			new ZetaCategory("experimental", Items.TNT),
			new ZetaCategory("oddities", Items.CHORUS_FRUIT, Quark.ODDITIES_ID),

			new ZetaCategory("testing", Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS.asItem())
		));
		Quark.ZETA.modules.load(new ModFileScanDataModuleFinder(Quark.MOD_ID));

		ModuleLoader.INSTANCE.start();

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		registerListeners(bus);

		LocalDateTime now = LocalDateTime.now();
		if (now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 16 || now.getMonth() == Month.JANUARY && now.getDayOfMonth() <= 2)
			jingleTheBells = true;
	}

	public void registerListeners(IEventBus bus) {
		bus.addListener(this::setup);
		bus.addListener(this::loadComplete);
		bus.addListener(this::configChanged);
		bus.addListener(this::registerCapabilities);

		WorldGenHandler.registerBiomeModifier(bus);

		bus.register(RegistryListener.class);
	}

	public void setup(FMLCommonSetupEvent event) {
		QuarkNetwork.setup();
		BrewingHandler.setup();

		ModuleLoader.INSTANCE.setup(event);
		initContributorRewards();

		WoodSetHandler.setup(event);
		ToolInteractionHandler.addModifiers();
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		ModuleLoader.INSTANCE.loadComplete(event);

		FuelHandler.addAllWoods();
		UndergroundBiomeHandler.init(event);
	}

	public void configChanged(ModConfigEvent event) {
		if(event.getConfig().getModId().equals(Quark.MOD_ID)
				&& ClientTicker.ticksInGame - lastConfigChange > 10
				&& !configGuiSaving) {
			lastConfigChange = ClientTicker.ticksInGame;
			handleQuarkConfigChange();
		}
	}

	public void setConfigGuiSaving(boolean saving) {
		configGuiSaving = saving;
		lastConfigChange = ClientTicker.ticksInGame;
	}

	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		CapabilityHandler.registerCapabilities(event);
	}

	public void handleQuarkConfigChange() {
		ModuleLoader.INSTANCE.configChanged();
		EntitySpawnHandler.refresh();
		SyncedFlagHandler.sendFlagInfoToPlayers();
	}

	/**
	 * Use an item WITHOUT sending the use to the server. This will cause ghost interactions if used incorrectly!
	 */
	public InteractionResult clientUseItem(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.PASS;
	}

	protected void initContributorRewards() {
		ContributorRewardHandler.init();
	}

	public IConfigCallback getConfigCallback() {
		return new IConfigCallback.Dummy();
	}

	public boolean isClientPlayerHoldingShift() {
		return false;
	}

	public static final class RegistryListener {

		private static boolean registerDone;

		@SubscribeEvent(priority = EventPriority.HIGHEST)
		public static void registerContent(RegisterEvent event) {
			if(registerDone)
				return;
			registerDone = true;

			ModuleLoader.INSTANCE.register();
			WoodSetHandler.register();
			WorldGenHandler.register();
			DyeHandler.register();
		}

	}

}
