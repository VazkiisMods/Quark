package vazkii.zeta.event.client;

import net.minecraft.resources.ResourceLocation;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZAddModels extends IZetaLoadEvent {
	void register(ResourceLocation model);
}
