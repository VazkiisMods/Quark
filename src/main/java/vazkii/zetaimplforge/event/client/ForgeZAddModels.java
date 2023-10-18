package vazkii.zetaimplforge.event.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.client.ZAddModels;

@FiredAs(ZAddModels.class)
public record ForgeZAddModels(ModelEvent.RegisterAdditional e) implements ZAddModels {
	@Override
	public void register(ResourceLocation model) {
		e.register(model);
	}
}
