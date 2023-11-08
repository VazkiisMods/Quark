package org.violetmoon.quark.content.tweaks.client.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;

public class TranslucentButton extends Button {

	public TranslucentButton(int xIn, int yIn, int widthIn, int heightIn, Component text, OnPress onPress) {
		super(xIn, yIn, widthIn, heightIn, text, onPress);
	}

	@Override
	public void blit(@Nonnull PoseStack stack, int x, int y, int textureX, int textureY, int width, int height) {
		fill(stack, x, y, x + width, y + height, Integer.MIN_VALUE);
	}

}
