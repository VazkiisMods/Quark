package vazkii.quark.base.capability;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.addons.oddities.capability.MagnetTracker;
import vazkii.quark.api.ICustomSorting;
import vazkii.quark.api.IMagnetTracker;
import vazkii.quark.api.IPistonCallback;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.api.ITransferManager;
import vazkii.quark.api.QuarkCapabilities;
import vazkii.quark.base.Quark;
import vazkii.quark.base.capability.dummy.DummyMagnetTracker;
import vazkii.quark.base.capability.dummy.DummyPistonCallback;
import vazkii.quark.base.capability.dummy.DummyRuneColor;
import vazkii.quark.base.capability.dummy.DummySorting;

public class CapabilityHandler {
	
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		registerLambda(event, ITransferManager.class, (player) -> false);

		register(event, ICustomSorting.class, DummySorting::new);
		register(event, IPistonCallback.class, DummyPistonCallback::new);
		register(event, IMagnetTracker.class, DummyMagnetTracker::new);
		register(event, IRuneColorProvider.class, DummyRuneColor::new);
	}
	
	private static <T> void registerLambda(RegisterCapabilitiesEvent event, Class<T> clazz, T provider) {
		register(event, clazz, () -> provider);
	}

	private static <T> void register(RegisterCapabilitiesEvent event, Class<T> clazz, Callable<T> provider) {
		event.register(clazz);
	}

	private static final ResourceLocation DROPOFF_MANAGER = new ResourceLocation(Quark.MOD_ID, "dropoff");
	private static final ResourceLocation SORTING_HANDLER = new ResourceLocation(Quark.MOD_ID, "sort");
	private static final ResourceLocation MAGNET_TRACKER = new ResourceLocation(Quark.MOD_ID, "magnet_tracker");
	private static final ResourceLocation RUNE_COLOR_HANDLER = new ResourceLocation(Quark.MOD_ID, "rune_color");

	@SubscribeEvent
	public static void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		Item item = event.getObject().getItem();

		if(item instanceof ICustomSorting)
			SelfProvider.attachItem(SORTING_HANDLER, QuarkCapabilities.SORTING, event);

		if(item instanceof IRuneColorProvider)
			SelfProvider.attachItem(RUNE_COLOR_HANDLER, QuarkCapabilities.RUNE_COLOR, event);
	}

	@SubscribeEvent
	public static void attachTileCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
		if (event.getObject() instanceof ITransferManager)
			SelfProvider.attach(DROPOFF_MANAGER, QuarkCapabilities.TRANSFER, event);
	}
	
	@SubscribeEvent 
	public static void attachWorldCapabilities(AttachCapabilitiesEvent<Level> event) {
		Level world = event.getObject();
		MagnetTracker tracker = new MagnetTracker(world);

		event.addCapability(MAGNET_TRACKER, new ICapabilityProvider() {
			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
				return QuarkCapabilities.MAGNET_TRACKER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> tracker));
			}
		});
	}
}
