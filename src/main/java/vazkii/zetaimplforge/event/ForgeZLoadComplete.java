package vazkii.zetaimplforge.event;

import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import vazkii.zeta.event.ZLoadComplete;

public record ForgeZLoadComplete(FMLLoadCompleteEvent e) implements ZLoadComplete {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
