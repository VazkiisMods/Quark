package org.violetmoon.zetaimplforge.client.event.player;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.client.event.RenderHighlightEvent;
import org.violetmoon.zeta.client.event.play.ZHighlightBlock;

public record ForgeZHighlightBlock(RenderHighlightEvent.Block e) implements ZHighlightBlock {
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
