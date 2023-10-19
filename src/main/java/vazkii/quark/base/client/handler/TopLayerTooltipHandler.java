package vazkii.quark.base.client.handler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TopLayerTooltipHandler {

	private static List<Component> tooltip;
	private static int tooltipX, tooltipY;

	@SubscribeEvent
	public static void renderTick(RenderTickEvent event) {
		if(event.phase == Phase.END && tooltip != null) {
			Minecraft mc = Minecraft.getInstance();
			Screen screen = Minecraft.getInstance().screen;
			screen.renderTooltip(new PoseStack(), tooltip, Optional.empty(), tooltipX, tooltipY, mc.font, ItemStack.EMPTY);
			tooltip = null;
		}
	}

	public static void setTooltip(List<String> tooltip, int tooltipX, int tooltipY) {
		TopLayerTooltipHandler.tooltip = tooltip.stream().map(Component::literal).collect(Collectors.toList());
		TopLayerTooltipHandler.tooltipX = tooltipX;
		TopLayerTooltipHandler.tooltipY = tooltipY;
	}

}
