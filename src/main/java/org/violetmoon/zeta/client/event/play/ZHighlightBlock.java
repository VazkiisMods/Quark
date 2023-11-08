package org.violetmoon.zeta.client.event.play;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import org.violetmoon.zeta.event.bus.Cancellable;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZHighlightBlock extends IZetaPlayEvent, Cancellable {
	MultiBufferSource getMultiBufferSource();
	Camera getCamera();
	PoseStack getPoseStack();
}
