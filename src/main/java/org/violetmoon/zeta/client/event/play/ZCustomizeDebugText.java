package org.violetmoon.zeta.client.event.play;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import java.util.List;

//TODO ZETA: only used for the network profiler
public interface ZCustomizeDebugText extends IZetaPlayEvent {
	List<String> getLeft();
	List<String> getRight();
	Window getWindow();
	PoseStack getPoseStack();
	float getPartialTick();
}
