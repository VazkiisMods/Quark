package vazkii.zeta.event.client;

import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZClientSetup extends IZetaLoadEvent {
	void enqueueWork(Runnable run);
}
