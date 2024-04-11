package org.violetmoon.quark.base.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import org.violetmoon.quark.addons.oddities.capability.MagnetTracker;
import org.violetmoon.quark.api.ICustomSorting;
import org.violetmoon.quark.api.IRuneColorProvider;
import org.violetmoon.quark.api.ITransferManager;
import org.violetmoon.quark.api.QuarkCapabilities;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.loading.ZAttachCapabilities;

// TODO: push these event handlers into their respective modules
public class CapabilityHandler {
	private static final ResourceLocation DROPOFF_MANAGER = new ResourceLocation(Quark.MOD_ID, "dropoff");
	private static final ResourceLocation SORTING_HANDLER = new ResourceLocation(Quark.MOD_ID, "sort");
	private static final ResourceLocation MAGNET_TRACKER = new ResourceLocation(Quark.MOD_ID, "magnet_tracker");
	private static final ResourceLocation RUNE_COLOR_HANDLER = new ResourceLocation(Quark.MOD_ID, "rune_color");

	@PlayEvent
	public static void attachItemCapabilities(ZAttachCapabilities.ItemStackCaps event) {
		Item item = event.getObject().getItem();

		if(item instanceof ICustomSorting impl)
			event.addCapability(SORTING_HANDLER, QuarkCapabilities.SORTING, impl);

		if(item instanceof IRuneColorProvider impl)
			event.addCapability(RUNE_COLOR_HANDLER, QuarkCapabilities.RUNE_COLOR, impl);
	}

	@PlayEvent
	public static void attachTileCapabilities(ZAttachCapabilities.BlockEntityCaps event) {
		if(event.getObject() instanceof ITransferManager impl)
			event.addCapability(DROPOFF_MANAGER, QuarkCapabilities.TRANSFER, impl);
	}

	@PlayEvent
	public static void attachLevelCapabilities(ZAttachCapabilities.LevelCaps event) {
		event.addCapability(MAGNET_TRACKER, QuarkCapabilities.MAGNET_TRACKER_CAPABILITY, new MagnetTracker(event.getObject()));
	}
}
