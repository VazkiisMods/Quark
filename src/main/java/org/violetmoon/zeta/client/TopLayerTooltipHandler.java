package org.violetmoon.zeta.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.client.event.play.ZRenderTick;
import org.violetmoon.zeta.event.bus.PlayEvent;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TopLayerTooltipHandler {

	private List<Component> tooltip;
	private int tooltipX, tooltipY;

	@PlayEvent
	public void renderTick(ZRenderTick event) {
		if(tooltip != null && event.isEndPhase()) {
			Minecraft mc = Minecraft.getInstance();
			Screen screen = Minecraft.getInstance().screen;

			//TODO: I think this is a Forge extension
			screen.renderTooltip(new PoseStack(), tooltip, Optional.empty(), tooltipX, tooltipY, mc.font, ItemStack.EMPTY);

			tooltip = null;
		}
	}

	public void setTooltip(List<String> tooltip, int x, int y) {
		this.tooltip = tooltip.stream().map(Component::literal).collect(Collectors.toList());
		this.tooltipX = x;
		this.tooltipY = y;
	}

}
