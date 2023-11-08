package org.violetmoon.zetaimplforge.event.load;

import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.violetmoon.zeta.event.load.ZLoadComplete;

public record ForgeZLoadComplete(FMLLoadCompleteEvent e) implements ZLoadComplete {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
