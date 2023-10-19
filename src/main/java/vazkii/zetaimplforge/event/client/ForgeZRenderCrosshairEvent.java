package vazkii.zetaimplforge.event.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import vazkii.zeta.event.bus.FiredAs;
import vazkii.zeta.event.client.ZRenderCrosshairEvent;

@FiredAs(ZRenderCrosshairEvent.class)
public record ForgeZRenderCrosshairEvent(RenderGuiOverlayEvent e) implements ZRenderCrosshairEvent {
	@Override
	public Window getWindow() {
		return e.getWindow();
	}

	@Override
	public PoseStack getPoseStack() {
		return e.getPoseStack();
	}
}
