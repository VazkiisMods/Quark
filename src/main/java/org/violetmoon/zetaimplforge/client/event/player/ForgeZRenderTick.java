package org.violetmoon.zetaimplforge.client.event.player;

import net.minecraftforge.event.TickEvent;
import org.violetmoon.zeta.client.event.play.ZRenderTick;

public record ForgeZRenderTick(TickEvent.RenderTickEvent e) implements ZRenderTick {
	@Override
	public float getRenderTickTime() {
		return e.renderTickTime;
	}

	@Override
	public boolean isEndPhase() {
		return e.phase == TickEvent.Phase.END;
	}
}
