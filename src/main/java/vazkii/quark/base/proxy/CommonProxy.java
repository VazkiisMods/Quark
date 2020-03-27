package vazkii.quark.base.proxy;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.Quark;
import vazkii.quark.base.capability.CapabilityHandler;
import vazkii.quark.base.handler.BrewingHandler;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.FuelHandler;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.recipe.ExclusionRecipe;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.base.world.WorldGenHandler;

public class CommonProxy {

	private int lastConfigChange = 0;
	
	public void start() {
		ForgeRegistries.RECIPE_SERIALIZERS.register(ExclusionRecipe.SERIALIZER);

		QuarkSounds.start();
		ModuleLoader.INSTANCE.start();
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		registerListeners(bus);
	}
	
	public void registerListeners(IEventBus bus) {
		bus.addListener(this::setup);
		bus.addListener(this::loadComplete);
		bus.addListener(this::configChanged);
	}
	
	public void setup(FMLCommonSetupEvent event) {
		QuarkNetwork.setup();
		BrewingHandler.setup();
		CapabilityHandler.setup();
		EntitySpawnHandler.refresh();
		ModuleLoader.INSTANCE.setup();
		initContributorRewards();
	}
	
	public void loadComplete(FMLLoadCompleteEvent event) {
		ModuleLoader.INSTANCE.loadComplete();
		WorldGenHandler.loadComplete();
		FuelHandler.addAllWoods();
	}
	
	public void configChanged(ModConfigEvent event) {
		if(event.getConfig().getModId().equals(Quark.MOD_ID) && ClientTicker.ticksInGame - lastConfigChange > 10) { 
			lastConfigChange = ClientTicker.ticksInGame;
			handleQuarkConfigChange();
			EntitySpawnHandler.refresh();
		}
	}
	
	public void handleQuarkConfigChange() {
		ModuleLoader.INSTANCE.configChanged();
	}
	
//	public void addResourceOverride(String type, String path, String file, BooleanSupplier isEnabled) {
//		// NO-OP, client only
//	}
	
	protected void initContributorRewards() {
		ContributorRewardHandler.init();
	}

}
