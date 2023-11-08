package org.violetmoon.zetaimplforge.client.event.load;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import org.violetmoon.zeta.client.event.load.ZKeyMapping;

public record ForgeZKeyMapping(RegisterKeyMappingsEvent e) implements ZKeyMapping {
	@Override
	public KeyMapping register(KeyMapping key) {
		e.register(key);
		return key;
	}
}
