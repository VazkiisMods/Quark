package vazkii.zeta.event.client;

import net.minecraft.client.KeyMapping;
import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZKeyMapping extends IZetaLoadEvent {
	void register(KeyMapping key);
}
