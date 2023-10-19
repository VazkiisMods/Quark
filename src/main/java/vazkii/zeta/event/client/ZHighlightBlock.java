package vazkii.zeta.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import vazkii.zeta.event.bus.Cancellable;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZHighlightBlock extends IZetaPlayEvent, Cancellable {
	MultiBufferSource getMultiBufferSource();
	Camera getCamera();
	PoseStack getPoseStack();
}
