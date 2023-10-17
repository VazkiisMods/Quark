package vazkii.zeta.event;

import vazkii.zeta.event.bus.IZetaLoadEvent;

public interface ZLoadComplete extends IZetaLoadEvent {
	void enqueueWork(Runnable run);
}
