package org.violetmoon.quark.base.proxy;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.api.ICustomSorting;
import org.violetmoon.quark.api.IMagnetTracker;
import org.violetmoon.quark.api.IPistonCallback;
import org.violetmoon.quark.api.IRuneColorProvider;
import org.violetmoon.quark.api.ITransferManager;
import org.violetmoon.quark.api.QuarkCapabilities;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.base.capability.CapabilityHandler;
import org.violetmoon.quark.base.capability.QuarkForgeCapabilities;
import org.violetmoon.quark.base.config.QuarkGeneralConfig;
import org.violetmoon.quark.base.handler.ContributorRewardHandler;
import org.violetmoon.quark.base.handler.QuarkSounds;
import org.violetmoon.quark.base.handler.WoodSetHandler;
import org.violetmoon.quark.base.network.QuarkNetwork;
import org.violetmoon.quark.base.recipe.ExclusionRecipe;
import org.violetmoon.zeta.config.SyncedFlagHandler;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZCommonSetup;
import org.violetmoon.zeta.event.load.ZConfigChanged;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaCategory;
import org.violetmoon.zetaimplforge.module.ModFileScanDataModuleFinder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class CommonProxy {

	public static boolean jingleTheBells = false;

	public void start() {
		// CAPABILITIES
		//todo put this bit in forge-specific code
		Quark.ZETA.capabilityManager
				.register(QuarkCapabilities.SORTING, QuarkForgeCapabilities.SORTING)
				.register(QuarkCapabilities.TRANSFER, QuarkForgeCapabilities.TRANSFER)
				.register(QuarkCapabilities.PISTON_CALLBACK, QuarkForgeCapabilities.PISTON_CALLBACK)
				.register(QuarkCapabilities.MAGNET_TRACKER_CAPABILITY, QuarkForgeCapabilities.MAGNET_TRACKER_CAPABILITY)
				.register(QuarkCapabilities.RUNE_COLOR, QuarkForgeCapabilities.RUNE_COLOR);
		//weird forge capability-implementation-class stuff
		MinecraftForge.EVENT_BUS.addListener((RegisterCapabilitiesEvent e) -> {
			e.register(ICustomSorting.class);
			e.register(ITransferManager.class);
			e.register(IPistonCallback.class);
			e.register(IMagnetTracker.class);
			e.register(IRuneColorProvider.class);
		});

		// GLOBAL EVENT LISTENERS
		Quark.ZETA.loadBus
				.subscribe(ContributorRewardHandler.class)
				.subscribe(QuarkSounds.class)
				.subscribe(WoodSetHandler.class)
				.subscribe(this);

		Quark.ZETA.playBus
				.subscribe(CapabilityHandler.class)
				.subscribe(ContributorRewardHandler.class);

		// OTHER RANDOM SHIT
		QuarkNetwork.init();

		// MODULES
		Quark.ZETA.loadModules(
				List.of(
						new ZetaCategory("automation", Items.REDSTONE),
						new ZetaCategory("building", Items.BRICKS),
						new ZetaCategory("management", Items.CHEST),
						new ZetaCategory("tools", Items.IRON_PICKAXE),
						new ZetaCategory("tweaks", Items.NAUTILUS_SHELL),
						new ZetaCategory("world", Items.GRASS_BLOCK),
						new ZetaCategory("mobs", Items.PIG_SPAWN_EGG),
						new ZetaCategory("client", Items.ENDER_EYE),
						new ZetaCategory("experimental", Items.TNT),
						new ZetaCategory("oddities", Items.CHORUS_FRUIT, Quark.ODDITIES_ID)
				),
				new ModFileScanDataModuleFinder(Quark.MOD_ID), //forge only
				QuarkGeneralConfig.INSTANCE
		);

		LocalDateTime now = LocalDateTime.now();
		if(now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 16 || now.getMonth() == Month.JANUARY && now.getDayOfMonth() <= 2)
			jingleTheBells = true;
	}

	//TODO find a better place for this little one-off thing, lol
	@LoadEvent
	public void recipe(ZRegister event) {
		event.getRegistry().register(ExclusionRecipe.SERIALIZER, "exclusion", Registries.RECIPE_SERIALIZER);
	}

	/**
	 * Use an item WITHOUT sending the use to the server. This will cause ghost interactions if used incorrectly!
	 */
	public InteractionResult clientUseItem(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		return InteractionResult.PASS;
	}

	public boolean isClientPlayerHoldingShift() {
		return false;
	}

	public float getVisualTime() {
		return 0f;
	}

	public @Nullable RegistryAccess hackilyGetCurrentClientLevelRegistryAccess() {
		return null;
	}
}
