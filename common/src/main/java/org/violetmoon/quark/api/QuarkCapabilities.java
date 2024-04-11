package org.violetmoon.quark.api;

import org.violetmoon.zeta.capability.ZetaCapability;

public class QuarkCapabilities {
	public static final ZetaCapability<ICustomSorting> SORTING = new ZetaCapability<>("quark:sorting");
	public static final ZetaCapability<ITransferManager> TRANSFER = new ZetaCapability<>("quark:transfer");
	public static final ZetaCapability<IPistonCallback> PISTON_CALLBACK = new ZetaCapability<>("quark:piston_callback");
	public static final ZetaCapability<IMagnetTracker> MAGNET_TRACKER_CAPABILITY = new ZetaCapability<>("quark:magnet_tracker");
	public static final ZetaCapability<IRuneColorProvider> RUNE_COLOR = new ZetaCapability<>("quark:rune_color");
}
