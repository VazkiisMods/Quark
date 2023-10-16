package vazkii.arl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vazkii.arl.AutoRegLib;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AutoRegLib.MOD_ID)
public final class ClientTicker {

	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;
	
	@OnlyIn(Dist.CLIENT)
	private static void calcDelta() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void renderTick(RenderTickEvent event) {
		if(event.phase == Phase.START)
			partialTicks = event.renderTickTime;
		else calcDelta();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void clientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			Screen gui = Minecraft.getInstance().screen;
			if(gui == null || !gui.isPauseScreen()) {
				ticksInGame++;
				partialTicks = 0;
			}
			
			calcDelta();
		}
	}

}
