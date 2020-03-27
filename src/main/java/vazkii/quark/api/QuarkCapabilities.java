package vazkii.quark.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class QuarkCapabilities {

	@CapabilityInject(ICustomSorting.class)
	public static final Capability<ICustomSorting> SORTING = null;
	
	@CapabilityInject(ITransferManager.class)
	public static final Capability<ITransferManager> TRANSFER = null;
	
	@CapabilityInject(IPistonCallback.class)
	public static final Capability<IPistonCallback> PISTON_CALLBACK = null;

    @CapabilityInject(IMagnetTracker.class)
    public static final Capability<IMagnetTracker> MAGNET_TRACKER_CAPABILITY = null;
    
}
