package org.violetmoon.zeta.client.event.load;

import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

public interface ZAddModels extends IZetaLoadEvent {
	void register(ResourceLocation model);
}
