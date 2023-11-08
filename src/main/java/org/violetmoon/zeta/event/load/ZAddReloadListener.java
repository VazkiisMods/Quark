package org.violetmoon.zeta.event.load;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZAddReloadListener extends IZetaLoadEvent {
	ReloadableServerResources getServerResources();
	void addListener(PreparableReloadListener listener);
}
