package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.violetmoon.zeta.client.event.load.ZAddModels;

public record ForgeZAddModels(ModelEvent.RegisterAdditional e) implements ZAddModels {
	@Override
	public void register(ResourceLocation model) {
		e.register(model);
	}
}
