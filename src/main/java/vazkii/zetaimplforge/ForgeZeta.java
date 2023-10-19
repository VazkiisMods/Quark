package vazkii.zetaimplforge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.Logger;
import vazkii.zeta.Zeta;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;
import vazkii.zetaimplforge.event.ForgeZCommonSetup;
import vazkii.zetaimplforge.event.ForgeZLivingDeath;
import vazkii.zetaimplforge.event.ForgeZLoadComplete;
import vazkii.zetaimplforge.event.ForgeZPlayNoteBlock;
import vazkii.zetaimplforge.event.ForgeZRightClickBlock;
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
	public boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr) {
		return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(player, hand, pos, bhr));
	}

	@Override
	public void start() {
		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		modbus.addListener(EventPriority.HIGHEST, this::registerHighest);
		modbus.addListener(this::commonSetup);
		modbus.addListener(this::loadComplete);

		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::rightClickBlockLow);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::livingDeathLowest);
		MinecraftForge.EVENT_BUS.addListener(this::playNoteBlock);
	}

	boolean registerDone = false;
	public void registerHighest(RegisterEvent e) {
		if(registerDone)
			return;

		loadBus.fire(new ZRegister());
		loadBus.fire(new ZRegister.Post());

		registerDone = true;
	}

	public void commonSetup(FMLCommonSetupEvent e) {
		loadBus.fire(new ForgeZCommonSetup(e));
	}

	public void loadComplete(FMLLoadCompleteEvent e) {
		loadBus.fire(new ForgeZLoadComplete(e));
	}

	public void rightClickBlockLow(PlayerInteractEvent.RightClickBlock e) {
		playBus.fire(new ForgeZRightClickBlock.Low(e));
	}

	public void livingDeathLowest(LivingDeathEvent e) {
		playBus.fire(new ForgeZLivingDeath.Lowest(e));
	}

	public void playNoteBlock(NoteBlockEvent.Play e) {
		playBus.fire(new ForgeZPlayNoteBlock(e));
	}

	public static ZResult from(Event.Result r) {
		return switch(r) {
			case DENY -> ZResult.DENY;
			case DEFAULT -> ZResult.DEFAULT;
			case ALLOW -> ZResult.ALLOW;
		};
	}

	public static Event.Result to(ZResult r) {
		return switch(r) {
			case DENY -> Event.Result.DENY;
			case DEFAULT -> Event.Result.DEFAULT;
			case ALLOW -> Event.Result.ALLOW;
		};
	}
}
