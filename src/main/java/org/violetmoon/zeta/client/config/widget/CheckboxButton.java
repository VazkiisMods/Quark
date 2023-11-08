package org.violetmoon.zeta.client.config.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.ValueDefinition;

import javax.annotation.Nonnull;

public class CheckboxButton extends Button {

	//checked:   u0 v0  to u16 v16
	//unchecked: u16 v0 to u32 v16
	private final ResourceLocation iconsTexture;
	private final ValueDefinition<Boolean> value;
	private final ChangeSet changes;

	public CheckboxButton(ResourceLocation iconsTexture, int x, int y, ChangeSet changes, ValueDefinition<Boolean> value) {
		super(x, y, 20, 20, Component.literal(""), CheckboxButton::toggle);
		this.iconsTexture = iconsTexture;
		this.value = value;
		this.changes = changes;
	}

	public CheckboxButton(ZetaClient zc, int x, int y, ChangeSet changes, ValueDefinition<Boolean> value) {
		this(zc.generalIcons, x, y, changes, value);
	}

	private static void toggle(Button press) {
		if(press instanceof CheckboxButton checkbox) {
			checkbox.changes.toggle(checkbox.value);
		}
	}

	@Override
	public void renderButton(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partial) {
		super.renderButton(mstack, mouseX, mouseY, partial);

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, iconsTexture);
		boolean enabled = changes.get(value) && active;
		int u = enabled ? 0 : 16;
		int v = 0;

		blit(mstack, x + 2, y + 1, u, v, 15, 15);
	}

}
