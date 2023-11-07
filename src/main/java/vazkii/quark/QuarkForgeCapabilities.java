package vazkii.quark;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import vazkii.quark.api.ICustomSorting;
import vazkii.quark.api.IMagnetTracker;
import vazkii.quark.api.IPistonCallback;
import vazkii.quark.api.IRuneColorProvider;
import vazkii.quark.api.ITransferManager;

//TODO: put this somewhere in a Forge-only API package
public class QuarkForgeCapabilities {
	public static final Capability<ICustomSorting> SORTING = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<ITransferManager> TRANSFER = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<IPistonCallback> PISTON_CALLBACK = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<IMagnetTracker> MAGNET_TRACKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

	public static final Capability<IRuneColorProvider> RUNE_COLOR = CapabilityManager.get(new CapabilityToken<>(){});
}
