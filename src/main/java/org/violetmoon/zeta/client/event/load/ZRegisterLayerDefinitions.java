package org.violetmoon.zeta.client.event.load;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.violetmoon.zeta.event.bus.IZetaLoadEvent;

import java.util.function.Supplier;

public interface ZRegisterLayerDefinitions extends IZetaLoadEvent {
	void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier);
}
