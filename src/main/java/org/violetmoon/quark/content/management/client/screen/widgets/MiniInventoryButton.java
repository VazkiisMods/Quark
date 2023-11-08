package org.violetmoon.quark.content.management.client.screen.widgets;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.violetmoon.quark.base.QuarkClient;
import org.violetmoon.quark.base.handler.MiscUtil;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class MiniInventoryButton extends Button {

	private final Consumer<List<String>> tooltip;
	private final int type;
	private final AbstractContainerScreen<?> parent;
	private final int startX;

	private BooleanSupplier shiftTexture = () -> false;

	public MiniInventoryButton(AbstractContainerScreen<?> parent, int type, int x, int y, Consumer<List<String>> tooltip, OnPress onPress) {
		super(parent.getGuiLeft() + x, parent.getGuiTop() + y, 10, 10, Component.literal(""), onPress);
		this.parent = parent;
		this.type = type;
		this.tooltip = tooltip;
		this.startX = x;
	}

	public MiniInventoryButton(AbstractContainerScreen<?> parent, int type, int x, int y, String tooltip, OnPress onPress) {
		this(parent, type, x, y, (t) -> t.add(I18n.get(tooltip)), onPress);
	}

	public MiniInventoryButton setTextureShift(BooleanSupplier func) {
		shiftTexture = func;
		return this;
	}

	@Override
	public void render(@Nonnull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		if(parent instanceof RecipeUpdateListener)
			x = parent.getGuiLeft() + startX;

		super.render(matrix, mouseX, mouseY, partialTicks);
	}

	@Override
	public void renderButton(@Nonnull PoseStack matrix, int mouseX, int mouseY, float partialTicks) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, MiscUtil.GENERAL_ICONS);

		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		int u = type * width;
		int v = 25 + (isHovered ? height : 0);
		if(shiftTexture.getAsBoolean())
			v += (height * 2);

		blit(matrix, x, y, u, v, width, height);

		if(isHovered)
			QuarkClient.ZETA_CLIENT.topLayerTooltipHandler.setTooltip(getTooltip(), mouseX, mouseY);
	}

	@Nonnull
	@Override
	protected MutableComponent createNarrationMessage() {
		List<String> tooltip = getTooltip();
		return tooltip.isEmpty() ? Component.literal("") : Component.translatable("gui.narrate.button", getTooltip().get(0));
	}

	public List<String> getTooltip() {
		List<String> list = new LinkedList<>();
		tooltip.accept(list);
		return list;
	}

}
