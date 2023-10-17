package vazkii.zeta.event.client;

import java.util.Map;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZModelBakingCompleted extends IZetaLoadEvent {
	Map<ResourceLocation, BakedModel> getModels();
}
