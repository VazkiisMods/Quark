package org.violetmoon.zeta.client.config.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.client.ZetaClient;

import javax.annotation.Nonnull;
import java.util.List;

public class CategoryButton extends Button {

	private final ZetaClient zc;
	private final ItemStack icon;
	private final Component text;

	public CategoryButton(ZetaClient zc, int x, int y, int w, int h, Component text, ItemStack icon, OnPress onClick) {
		super(x, y, w, h, Component.literal(""), onClick);
		this.zc = zc;
		this.icon = icon;
		this.text = text;
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.render(mstack, mouseX, mouseY, partialTicks);

		if(!active && isHovered)
			zc.topLayerTooltipHandler.setTooltip(List.of(I18n.get("quark.gui.config.missingaddon")), mouseX, mouseY);

		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer().renderGuiItem(icon, x + 5, y + 2);

		int iconPad = (16 + 5) / 2;
		drawCenteredString(mstack, mc.font, text, x + width / 2 + iconPad, y + (height - 8) / 2, getFGColor());
	}

}
