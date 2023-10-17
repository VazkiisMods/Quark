package vazkii.zetaimplforge.event;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import vazkii.zeta.event.ZCommonSetup;

public record ForgeZCommonSetup(FMLCommonSetupEvent e) implements ZCommonSetup {
	@Override
	public void enqueueWork(Runnable run) {
		e.enqueueWork(run);
	}
}
