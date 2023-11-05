package vazkii.zeta.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZScreen extends IZetaPlayEvent {
    interface Render extends IZetaPlayEvent, ZScreen {
        PoseStack getPoseStack();
        int getMouseX();
        int getMouseY();

        interface Post extends IZetaPlayEvent, ZScreen, Render { }
    }
}
