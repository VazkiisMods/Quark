package vazkii.zetaimplforge.event;

import net.minecraftforge.client.event.InputEvent;
import vazkii.zeta.event.ZInputEvent;

public class ForgeZInputEvent implements ZInputEvent {
    public static class MouseButton extends ForgeZInputEvent implements ZInputEvent.MouseButton {
        private final InputEvent.MouseButton e;

        public MouseButton(InputEvent.MouseButton e) {
            this.e = e;
        }

        @Override
        public int getButton() {
            return e.getButton();
        }
    }

    public static class Key extends ForgeZInputEvent implements ZInputEvent.Key {
        private final InputEvent.Key e;

        public Key(InputEvent.Key e) {
            this.e = e;
        }

        @Override
        public int getKey() {
            return e.getKey();
        }

        @Override
        public int getScanCode() {
            return e.getScanCode();
        }
    }
}
