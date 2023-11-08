package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import java.util.Map;

public interface ZModelBakingCompleted extends IZetaLoadEvent {
	Map<ResourceLocation, BakedModel> getModels();
}
