package vazkii.zeta.event;

import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZCommonSetup extends IZetaLoadEvent {
	void enqueueWork(Runnable run);
}
