package vazkii.zetaimplforge.event.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.event.RenderHighlightEvent;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.client.ZHighlightBlockEvent;

@FiredAs(ZHighlightBlockEvent.class)
public record ForgeZHighlightBlockEvent(RenderHighlightEvent.Block e) implements ZHighlightBlockEvent {
	@Override
	public Camera getCamera() {
		return e.getCamera();
	}

	@Override
	public PoseStack getPoseStack() {
		return e.getPoseStack();
	}

	@Override
	public MultiBufferSource getMultiBufferSource() {
		return e.getMultiBufferSource();
	}

	@Override
	public boolean isCanceled() {
		return e.isCanceled();
	}

	@Override
	public void setCanceled(boolean cancel) {
		e.setCanceled(cancel);
	}
}
