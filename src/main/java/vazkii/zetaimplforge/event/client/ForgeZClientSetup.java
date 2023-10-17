package vazkii.zetaimplforge.event.client;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.zeta.event.client.ZClientSetup;

public record ForgeZClientSetup(FMLClientSetupEvent e) implements ZClientSetup {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
