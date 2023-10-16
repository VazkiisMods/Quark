package vazkii.arl.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.arl.AutoRegLib;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AutoRegLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ARLClientInitializer {
	@SubscribeEvent
	public static void registerBlockColors(RegisterColorHandlersEvent.Block evt) {
		RegistryHelper.submitBlockColors(evt.getBlockColors()::register);
	}

	@SubscribeEvent
	public static void registerItemColors(RegisterColorHandlersEvent.Item evt) {
		RegistryHelper.submitItemColors(evt.getItemColors()::register);
	}
}
