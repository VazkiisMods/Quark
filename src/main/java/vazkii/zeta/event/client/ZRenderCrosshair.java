package vazkii.zeta.event.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZRenderCrosshair extends IZetaPlayEvent {
	Window getWindow();
	PoseStack getPoseStack();
}
