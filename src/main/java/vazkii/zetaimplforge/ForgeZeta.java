package vazkii.zetaimplforge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.NoteBlockEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
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
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.Zeta;
import vazkii.zeta.block.ext.BlockExtensionFactory;
import vazkii.zeta.capability.ZetaCapabilityManager;
import vazkii.zeta.config.IZetaConfigInternals;
import vazkii.zeta.config.SectionDefinition;
import vazkii.zeta.event.*;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zeta.item.ext.ItemExtensionFactory;
import vazkii.zeta.network.ZetaNetworkHandler;
import vazkii.zeta.registry.BrewingRegistry;
import vazkii.zeta.registry.CraftingExtensionsRegistry;
import vazkii.zeta.registry.ZetaRegistry;
import vazkii.zeta.util.ZetaSide;
import vazkii.zetaimplforge.block.IForgeBlockBlockExtensions;
import vazkii.zetaimplforge.capability.ForgeCapabilityManager;
import vazkii.zetaimplforge.config.ForgeBackedConfig;
import vazkii.zetaimplforge.config.TerribleForgeConfigHackery;
import vazkii.zetaimplforge.event.*;
import vazkii.zetaimplforge.item.IForgeItemItemExtensions;
import vazkii.zetaimplforge.network.ForgeZetaNetworkHandler;
import vazkii.zetaimplforge.registry.ForgeBrewingRegistry;
import vazkii.zetaimplforge.registry.ForgeCraftingExtensionsRegistry;
import vazkii.zetaimplforge.registry.ForgeZetaRegistry;

/**
 * ideally do not touch quark from this package, it will later be split off
 */
public class ForgeZeta extends Zeta {
	public ForgeZeta(String modid, Logger log) {
		super(modid, log, ZetaSide.fromClient(FMLEnvironment.dist.isClient()));
	}

	@Override
	public boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	@Override
	public @Nullable String getModDisplayName(String modid) {
		return ModList.get().getModContainerById(modid)
			.map(c -> c.getModInfo().getDisplayName())
			.orElse(null);
	}

	@Override
	public IZetaConfigInternals makeConfigInternals(SectionDefinition rootSection) {
		ForgeConfigSpec.Builder bob = new ForgeConfigSpec.Builder();
		ForgeBackedConfig forge = new ForgeBackedConfig(rootSection, bob);
		ForgeConfigSpec spec = bob.build();

		TerribleForgeConfigHackery.registerAndLoadConfigEarlierThanUsual(spec);

		return forge;
	}

	@Override
	public ZetaRegistry createRegistry() {
		return new ForgeZetaRegistry(this);
	}

	@Override
	public CraftingExtensionsRegistry createCraftingExtensionsRegistry() {
		return new ForgeCraftingExtensionsRegistry();
	}

	@Override
	public BrewingRegistry createBrewingRegistry() {
		return new ForgeBrewingRegistry(this);
	}

	@Override
	public ZetaNetworkHandler createNetworkHandler(String modid, int protocolVersion) {
		return new ForgeZetaNetworkHandler(modid, protocolVersion);
	}

	@Override
	public ZetaCapabilityManager createCapabilityManager() {
		return new ForgeCapabilityManager();
	}

	@Override
	public BlockExtensionFactory createBlockExtensionFactory() {
		return block -> IForgeBlockBlockExtensions.INSTANCE;
	}

	@Override
	public ItemExtensionFactory createItemExtensionFactory() {
		return stack -> IForgeItemItemExtensions.INSTANCE;
	}

	@Override
	public boolean fireRightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult bhr) {
		return MinecraftForge.EVENT_BUS.post(new PlayerInteractEvent.RightClickBlock(player, hand, pos, bhr));
	}

	@SuppressWarnings("duplicates")
	@Override
	public void start() {
		//TODO: sort these somehow lol

		//load
		IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();
		modbus.addListener(EventPriority.HIGHEST, this::registerHighest);
		modbus.addListener(this::commonSetup);
		modbus.addListener(this::loadComplete);
		modbus.addListener(this::entityAttributeCreation);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListener);
		MinecraftForge.EVENT_BUS.addListener(this::tagsUpdated);

		//play
		MinecraftForge.EVENT_BUS.addListener(this::rightClickBlock);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOW, this::rightClickBlockLow);
		MinecraftForge.EVENT_BUS.addListener(this::rightClickItem);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::livingDeathLowest);
		MinecraftForge.EVENT_BUS.addListener(this::livingTick);
		MinecraftForge.EVENT_BUS.addListener(this::playNoteBlock);
		MinecraftForge.EVENT_BUS.addListener(this::lootTableLoad);
		MinecraftForge.EVENT_BUS.addListener(this::livingConversion);
		MinecraftForge.EVENT_BUS.addListener(this::livingConversionPre);
		MinecraftForge.EVENT_BUS.addListener(this::livingConversionPost);
		MinecraftForge.EVENT_BUS.addListener(this::anvilUpdate);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::anvilUpdateLowest);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::anvilUpdateHighest);
		MinecraftForge.EVENT_BUS.addListener(this::entityConstruct);
		MinecraftForge.EVENT_BUS.addListener(this::entityInteract);
		MinecraftForge.EVENT_BUS.addListener(this::entityMobGriefing);
		MinecraftForge.EVENT_BUS.addListener(this::livingDrops);
		MinecraftForge.EVENT_BUS.addListener(this::livingDropsLowest);
		MinecraftForge.EVENT_BUS.addListener(this::playerTickStart);
		MinecraftForge.EVENT_BUS.addListener(this::playerTickEnd);
		MinecraftForge.EVENT_BUS.addListener(this::babyEntitySpawn);
		MinecraftForge.EVENT_BUS.addListener(this::babyEntitySpawnLowest);
		MinecraftForge.EVENT_BUS.addListener(this::entityJoinLevel);

		MinecraftForge.EVENT_BUS.addGenericListener(ItemStack.class, this::itemStackCaps);
		MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, this::blockEntityCaps);
		MinecraftForge.EVENT_BUS.addGenericListener(Level.class, this::levelCaps);

		MinecraftForge.EVENT_BUS.addListener(this::levelTickStart);
		MinecraftForge.EVENT_BUS.addListener(this::levelTickEnd);
		MinecraftForge.EVENT_BUS.addListener(this::playerInteract);
		MinecraftForge.EVENT_BUS.addListener(this::playerInteractEntityInteractSpecific);
		MinecraftForge.EVENT_BUS.addListener(this::playerInteractEntityInteract);
		MinecraftForge.EVENT_BUS.addListener(this::playerInteractRightClickBlock);
		MinecraftForge.EVENT_BUS.addListener(this::playerInteractRightClickItem);
		MinecraftForge.EVENT_BUS.addListener(this::playerDestroyItem);
		MinecraftForge.EVENT_BUS.addListener(this::livingSpawn);
		MinecraftForge.EVENT_BUS.addListener(this::livingSpawnCheckSpawn);
		MinecraftForge.EVENT_BUS.addListener(this::livingSpawnCheckSpawnLowest);
		MinecraftForge.EVENT_BUS.addListener(this::livingChangeTarget);
		MinecraftForge.EVENT_BUS.addListener(this::sleepingLocationCheck);
		MinecraftForge.EVENT_BUS.addListener(this::villagerTrades);
		MinecraftForge.EVENT_BUS.addListener(this::anvilRepair);
		MinecraftForge.EVENT_BUS.addListener(this::player);
		MinecraftForge.EVENT_BUS.addListener(this::playerBreakSpeed);
		MinecraftForge.EVENT_BUS.addListener(this::playerClone);
		MinecraftForge.EVENT_BUS.addListener(this::playerLoggedIn);
		MinecraftForge.EVENT_BUS.addListener(this::playerLoggedOut);
		MinecraftForge.EVENT_BUS.addListener(this::entityItemPickup);
		MinecraftForge.EVENT_BUS.addListener(this::block);
		MinecraftForge.EVENT_BUS.addListener(this::blockBreak);
		MinecraftForge.EVENT_BUS.addListener(this::blockEntityPlace);
		MinecraftForge.EVENT_BUS.addListener(this::animalTame);
		MinecraftForge.EVENT_BUS.addListener(this::bonemeal);
		MinecraftForge.EVENT_BUS.addListener(this::entityTeleport);
		MinecraftForge.EVENT_BUS.addListener(this::livingFall);
	}

	boolean registerDone = false;
	public void registerHighest(RegisterEvent e) {
		if(registerDone)
			return;

		loadBus.fire(new ZRegister(this));
		loadBus.fire(new ZRegister.Post());

		registerDone = true;
	}

	public void commonSetup(FMLCommonSetupEvent e) {
		loadBus.fire(new ForgeZCommonSetup(e), ZCommonSetup.class);
	}

	public void loadComplete(FMLLoadCompleteEvent e) {
		loadBus.fire(new ForgeZLoadComplete(e), ZLoadComplete.class);
	}

	public void entityAttributeCreation(EntityAttributeCreationEvent e) {
		loadBus.fire(new ForgeZEntityAttributeCreation(e), ZEntityAttributeCreation.class);
	}

	public void addReloadListener(AddReloadListenerEvent e) {
		loadBus.fire(new ForgeZAddReloadListener(e), ZAddReloadListener.class);
	}

	public void tagsUpdated(TagsUpdatedEvent e) {
		loadBus.fire(new ZTagsUpdated());
	}

	public void rightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		playBus.fire(new ForgeZRightClickBlock(e), ZRightClickBlock.class);
	}

	public void rightClickBlockLow(PlayerInteractEvent.RightClickBlock e) {
		playBus.fire(new ForgeZRightClickBlock.Low(e), ZRightClickBlock.Low.class);
	}

	public void rightClickItem(PlayerInteractEvent.RightClickItem e) {
		playBus.fire(new ForgeZRightClickItem(e), ZRightClickItem.class);
	}

	public void livingDeathLowest(LivingDeathEvent e) {
		playBus.fire(new ForgeZLivingDeath.Lowest(e), ZLivingDeath.Lowest.class);
	}

	public void livingTick(LivingEvent.LivingTickEvent e) {
		playBus.fire(new ForgeZLivingTick(e), ZLivingTick.class);
	}

	public void playNoteBlock(NoteBlockEvent.Play e) {
		playBus.fire(new ForgeZPlayNoteBlock(e), ZPlayNoteBlock.class);
	}

	public void lootTableLoad(LootTableLoadEvent e) {
		playBus.fire(new ForgeZLootTableLoad(e), ZLootTableLoad.class);
	}

	public void livingConversion(LivingConversionEvent e) {
		playBus.fire(new ForgeZLivingConversion(e), ZLivingConversion.class);
	}

	public void livingConversionPre(LivingConversionEvent.Pre e) {
		playBus.fire(new ForgeZLivingConversion.Pre(e), ZLivingConversion.Pre.class);
	}

	public void livingConversionPost(LivingConversionEvent.Post e) {
		playBus.fire(new ForgeZLivingConversion.Post(e), ZLivingConversion.Post.class);
	}

	public void anvilUpdate(AnvilUpdateEvent e) {
		playBus.fire(new ForgeZAnvilUpdate(e), ZAnvilUpdate.class);
	}

	public void anvilUpdateLowest(AnvilUpdateEvent e) {
		playBus.fire(new ForgeZAnvilUpdate.Lowest(e), ZAnvilUpdate.Lowest.class);
	}

	public void anvilUpdateHighest(AnvilUpdateEvent e) {
		playBus.fire(new ForgeZAnvilUpdate.Highest(e), ZAnvilUpdate.Highest.class);
	}

	public void entityConstruct(EntityEvent.EntityConstructing e) {
		playBus.fire(new ForgeZEntityConstruct(e), ZEntityConstruct.class);
	}

	public void entityInteract(PlayerInteractEvent.EntityInteract e) {
		playBus.fire(new ForgeZEntityInteract(e), ZEntityInteract.class);
	}

	public void entityMobGriefing(EntityMobGriefingEvent e) {
		playBus.fire(new ForgeZEntityMobGriefing(e), ZEntityMobGriefing.class);
	}

	public void livingDrops(LivingDropsEvent e) {
		playBus.fire(new ForgeZLivingDrops(e), ZLivingDrops.class);
	}

	public void livingDropsLowest(LivingDropsEvent e) {
		playBus.fire(new ForgeZLivingDrops.Lowest(e), ZLivingDrops.Lowest.class);
	}

	public void playerTickStart(TickEvent.PlayerTickEvent e) {
		if (e.phase == TickEvent.Phase.START)
			playBus.fire(new ForgeZPlayerTick.Start(e), ZPlayerTick.Start.class);
	}

	public void playerTickEnd(TickEvent.PlayerTickEvent e) {
		if (e.phase == TickEvent.Phase.END)
			playBus.fire(new ForgeZPlayerTick.End(e), ZPlayerTick.End.class);
	}

	public void babyEntitySpawn(BabyEntitySpawnEvent e) {
		playBus.fire(new ForgeZBabyEntitySpawn(e), ZBabyEntitySpawn.class);
	}

	public void babyEntitySpawnLowest(BabyEntitySpawnEvent e) {
		playBus.fire(new ForgeZBabyEntitySpawn.Lowest(e), ZBabyEntitySpawn.Lowest.class);
	}

	public void entityJoinLevel(EntityJoinLevelEvent e) {
		playBus.fire(new ForgeZEntityJoinLevel(e), ZEntityJoinLevel.class);
	}

	public void itemStackCaps(AttachCapabilitiesEvent<ItemStack> e) {
		playBus.fire(new ForgeZAttachCapabilities.ItemStackCaps(capabilityManager, e), ZAttachCapabilities.ItemStackCaps.class);
	}

	public void blockEntityCaps(AttachCapabilitiesEvent<BlockEntity> e) {
		playBus.fire(new ForgeZAttachCapabilities.BlockEntityCaps(capabilityManager, e), ZAttachCapabilities.BlockEntityCaps.class);
	}

	public void levelCaps(AttachCapabilitiesEvent<Level> e) {
		playBus.fire(new ForgeZAttachCapabilities.LevelCaps(capabilityManager, e), ZAttachCapabilities.LevelCaps.class);
	}

	public void levelTickStart(TickEvent.LevelTickEvent e) {
		if (e.phase == TickEvent.Phase.START)
			playBus.fire(new ForgeZLevelTick.Start(e), ZLevelTick.Start.class);
	}

	public void levelTickEnd(TickEvent.LevelTickEvent e) {
		if (e.phase == TickEvent.Phase.END)
			playBus.fire(new ForgeZLevelTick.End(e), ZLevelTick.End.class);
	}

	public void playerInteract(PlayerInteractEvent e) {
		playBus.fire(new ForgeZPlayerInteract(e), ZPlayerInteract.class);
	}

	public void playerInteractEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific e) {
		playBus.fire(new ForgeZPlayerInteract.EntityInteractSpecific(e), ZPlayerInteract.EntityInteractSpecific.class);
	}

	public void playerInteractEntityInteract(PlayerInteractEvent.EntityInteract e) {
		playBus.fire(new ForgeZPlayerInteract.EntityInteract(e), ZPlayerInteract.EntityInteract.class);
	}

	public void playerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock e) {
		playBus.fire(new ForgeZPlayerInteract.RightClickBlock(e), ZPlayerInteract.RightClickBlock.class);
	}

	public void playerInteractRightClickItem(PlayerInteractEvent.RightClickItem e) {
		playBus.fire(new ForgeZPlayerInteract.RightClickItem(e), ZPlayerInteract.RightClickItem.class);
	}

	public void playerDestroyItem(PlayerDestroyItemEvent e) {
		playBus.fire(new ForgeZPlayerDestroyItem(e), ZPlayerDestroyItem.class);
	}

	public void livingSpawn(LivingSpawnEvent e) {
		playBus.fire(new ForgeZLivingSpawn(e), ZLivingSpawn.class);
	}

	public void livingSpawnCheckSpawn(LivingSpawnEvent.CheckSpawn e) {
		playBus.fire(new ForgeZLivingSpawn.CheckSpawn(e), ZLivingSpawn.CheckSpawn.class);
	}

	public void livingSpawnCheckSpawnLowest(LivingSpawnEvent.CheckSpawn e) {
		playBus.fire(new ForgeZLivingSpawn.CheckSpawn.Lowest(e), ZLivingSpawn.CheckSpawn.Lowest.class);
	}

	public void livingChangeTarget(LivingChangeTargetEvent e) {
		playBus.fire(new ForgeZLivingChangeTarget(e), ZLivingChangeTarget.class);
	}

	public void sleepingLocationCheck(SleepingLocationCheckEvent e) {
		playBus.fire(new ForgeZSleepingLocationCheck(e), ZSleepingLocationCheck.class);
	}

	public void entityItemPickup(EntityItemPickupEvent e) {
		playBus.fire(new ForgeZEntityItemPickup(e), ZEntityItemPickup.class);
	}

	public void block(BlockEvent e) {
		playBus.fire(new ForgeZBlock(e), ZBlock.class);
	}

	public void blockBreak(BlockEvent.BreakEvent e) {
		playBus.fire(new ForgeZBlock.Break(e), ZBlock.Break.class);
	}

	public void blockEntityPlace(BlockEvent.EntityPlaceEvent e) {
		playBus.fire(new ForgeZBlock.EntityPlace(e), ZBlock.EntityPlace.class);
	}

	public void animalTame(AnimalTameEvent e) {
		playBus.fire(new ForgeZAnimalTame(e), ZAnimalTame.class);
	}


	public void villagerTrades(VillagerTradesEvent e) {
		playBus.fire(new ForgeZVillagerTrades(e), ZVillagerTrades.class);
	}

	public void anvilRepair(AnvilRepairEvent e) {
		playBus.fire(new ForgeZAnvilRepair(e), ZAnvilRepair.class);
	}

	public void player(PlayerEvent e) {
		playBus.fire(new ForgeZPlayer(e), ZPlayer.class);
	}

	public void playerBreakSpeed(PlayerEvent.BreakSpeed e) {
		playBus.fire(new ForgeZPlayer.BreakSpeed(e), ZPlayer.BreakSpeed.class);
	}

	public void playerClone(PlayerEvent.Clone e) {
		playBus.fire(new ForgeZPlayer.Clone(e), ZPlayer.Clone.class);
	}

	public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
		playBus.fire(new ForgeZPlayer.LoggedIn(e), ZPlayer.LoggedIn.class);
	}

	public void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
		playBus.fire(new ForgeZPlayer.LoggedOut(e), ZPlayer.LoggedOut.class);
	}

	public void bonemeal(BonemealEvent e) {
		playBus.fire(new ForgeZBonemeal(e), ZBonemeal.class);
	}

	public void entityTeleport(EntityTeleportEvent e) {
		playBus.fire(new ForgeZEntityTeleport(e), ZEntityTeleport.class);
	}

	public void livingFall(LivingFallEvent e) {
		playBus.fire(new ForgeZLivingFall(e), ZLivingFall.class);
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
