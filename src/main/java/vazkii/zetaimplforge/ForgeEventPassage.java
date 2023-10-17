package vazkii.zetaimplforge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.zeta.Zeta;
import vazkii.zetaimplforge.event.ForgeZCommonSetup;
import vazkii.zetaimplforge.event.ForgeZLoadComplete;

public class ForgeEventPassage {
	public ForgeEventPassage(Zeta z) {
		this.z = z;

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	private final Zeta z;

	@SubscribeEvent
	public void commonSetup(FMLCommonSetupEvent e) {
		z.loadBus.fire(new ForgeZCommonSetup(e));
	}

	@SubscribeEvent
	public void loadComplete(FMLLoadCompleteEvent e) {
		z.loadBus.fire(new ForgeZLoadComplete(e));
	}
}
