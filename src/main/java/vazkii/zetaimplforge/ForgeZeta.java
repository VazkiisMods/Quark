package vazkii.zetaimplforge;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.Logger;
import vazkii.zeta.Zeta;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;
import vazkii.zetaimplforge.event.ForgeZCommonSetup;
import vazkii.zetaimplforge.event.ForgeZLoadComplete;
import vazkii.zetaimplforge.event.ForgeZPlayNoteBlock;
import vazkii.zetaimplforge.network.ForgeZetaNetworkHandler;
import vazkii.zetaimplforge.registry.ForgeZetaRegistry;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {
	public ForgeZeta(String modid, Logger log) {
		super(modid, log);
	}

	@Override
	public ZetaSide getSide() {
		return switch(FMLEnvironment.dist) {
			case CLIENT -> ZetaSide.CLIENT;
			case DEDICATED_SERVER -> ZetaSide.SERVER;
		};
	}

	@Override
	public boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	@Override
	public ZetaRegistry createRegistry(String modid) {
		return new ForgeZetaRegistry(this, modid);
	}

	@Override
	public ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion) {
		return new ForgeZetaNetworkHandler(modid, protocolVersion);
	}

	@Override
	public void wireEvents() {
		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		modbus.addListener(this::commonSetup);
		modbus.addListener(this::loadComplete);

		MinecraftForge.EVENT_BUS.addListener(this::playNoteBlock);
	}

	public void commonSetup(FMLCommonSetupEvent e) {
		loadBus.fire(new ForgeZCommonSetup(e));
	}

	public void loadComplete(FMLLoadCompleteEvent e) {
		loadBus.fire(new ForgeZLoadComplete(e));
	}

	public void playNoteBlock(NoteBlockEvent.Play e) {
		playBus.fire(new ForgeZPlayNoteBlock(e));
	}
}
