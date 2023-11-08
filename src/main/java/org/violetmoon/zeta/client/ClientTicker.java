package org.violetmoon.zeta.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.violetmoon.zeta.client.event.play.ZEndClientTick;
import org.violetmoon.zeta.client.event.play.ZRenderTick;
import org.violetmoon.zeta.event.bus.PlayEvent;

public final class ClientTicker {
	public int ticksInGame = 0;
	public float partialTicks = 0;
	public float delta = 0;
	public float total = 0;

	@PlayEvent
	public void onRenderTick(ZRenderTick event) {
		if(event.isStartPhase())
			partialTicks = event.getRenderTickTime();
		else
			endRenderTick();
	}

	@PlayEvent
	public void onEndClientTick(ZEndClientTick event) {
		Screen gui = Minecraft.getInstance().screen;
		if(gui == null || !gui.isPauseScreen()) {
			ticksInGame++;
			partialTicks = 0;
		}

		endRenderTick();
	}

	public void endRenderTick() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}
}
