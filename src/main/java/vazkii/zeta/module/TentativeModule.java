package vazkii.zeta.module;

import java.util.Set;

public interface TentativeModule {
	ZetaModule construct();

	//accessors to the ZetaLoadModule annotation (since you don't get the actual annotation object w/ ModFileScanData)
	String id();
	String clientExtensionOf();
	boolean enabledByDefault();
	Set<String> antiOverlap();

	default boolean isCommon() {
		return clientExtensionOf().isEmpty();
	}

	default boolean isClientOnly() {
		return !isCommon();
	}
}
