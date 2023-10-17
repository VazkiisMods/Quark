package vazkii.zetaimplforge.event.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import vazkii.zeta.event.client.ZKeyMapping;

public record ForgeZKeyMapping(RegisterKeyMappingsEvent e) implements ZKeyMapping {
	@Override
	public void register(KeyMapping key) {
		e.register(key);
	}
}
