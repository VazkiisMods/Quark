package vazkii.quark.base.proxy;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
import vazkii.zetaimplforge.module.ModFileScanDataModuleFinder;

import java.time.LocalDateTime;
import java.time.Month;

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
