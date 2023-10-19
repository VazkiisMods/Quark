package vazkii.zeta.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZRenderChat extends IZetaPlayEvent {
	PoseStack getPoseStack();

	interface Pre extends ZRenderChat, Cancellable { }
	interface Post extends ZRenderChat { }
}
