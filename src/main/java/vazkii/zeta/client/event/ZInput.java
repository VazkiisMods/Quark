package vazkii.zeta.client.event;

import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZInput extends IZetaPlayEvent {
	interface MouseButton extends IZetaPlayEvent, ZInput {
		int getButton();
	}

	interface Key extends IZetaPlayEvent, ZInput {
		int getKey();
		int getScanCode();
		int getAction();
	}
}
