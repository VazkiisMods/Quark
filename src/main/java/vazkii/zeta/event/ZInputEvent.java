package vazkii.zeta.event;

import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZInputEvent extends IZetaPlayEvent {
    interface MouseButton extends IZetaPlayEvent, ZInputEvent {
        int getButton();
    }

    interface Key extends IZetaPlayEvent, ZInputEvent {
        int getKey();
        int getScanCode();
    }
}
