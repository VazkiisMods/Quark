package org.violetmoon.zeta.client;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.violetmoon.zeta.client.event.play.ZRenderTick;
import org.violetmoon.zeta.event.bus.PlayEvent;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TopLayerTooltipHandler {

	private List<Component> tooltip;
	private int tooltipX, tooltipY;

	@PlayEvent
	public void renderTick(ZRenderTick event) {
		if(tooltip != null && event.isEndPhase()) {
			Screen screen = Minecraft.getInstance().screen;

			if(screen != null)
				screen.renderTooltip(new PoseStack(), tooltip, Optional.empty(), tooltipX, tooltipY);

			tooltip = null;
		}
	}

	public void setTooltip(List<String> tooltip, int x, int y) {
		this.tooltip = tooltip.stream().map(Component::literal).collect(Collectors.toList());
		this.tooltipX = x;
		this.tooltipY = y;
	}

}
