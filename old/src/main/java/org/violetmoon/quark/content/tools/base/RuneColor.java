package org.violetmoon.quark.content.tools.base;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author WireSegal
 * Created at 10:00 AM on 12/23/23.
 */
public enum RuneColor implements StringRepresentable {
	WHITE(DyeColor.WHITE),
	ORANGE(DyeColor.ORANGE),
	MAGENTA(DyeColor.MAGENTA),
	LIGHT_BLUE(DyeColor.LIGHT_BLUE),
	YELLOW(DyeColor.YELLOW),
	LIME(DyeColor.LIME),
	PINK(DyeColor.PINK),
	GRAY(DyeColor.GRAY),
	LIGHT_GRAY(DyeColor.LIGHT_GRAY),
	CYAN(DyeColor.CYAN),
	PURPLE(DyeColor.PURPLE),
	BLUE(DyeColor.BLUE),
	BROWN(DyeColor.BROWN),
	GREEN(DyeColor.GREEN),
	RED(DyeColor.RED),
	BLACK(DyeColor.BLACK, 0x404040),
	RAINBOW("rainbow", ChatFormatting.WHITE),
	BLANK("blank", ChatFormatting.GRAY);

	@Nullable
	private final DyeColor dyeColor;
	private final String name;
	private final int textColor;

	RuneColor(DyeColor color) {
		this(color, color.getTextColor());
	}

	RuneColor(DyeColor color, int textColor) {
		this(color.getSerializedName(), textColor, color);
	}

	RuneColor(String name, ChatFormatting textColor) {
		this(name, textColor.getColor() != null ? textColor.getColor() : -1, null);
	}

	RuneColor(String name, int textColor, @Nullable DyeColor dyeColor) {
		this.dyeColor = dyeColor;
		this.name = name;
		this.textColor = textColor;
	}

	public TextColor getTextColor() {
		return TextColor.fromRgb(this.textColor);
	}

	@Nonnull
	@Override
	public String getSerializedName() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	@Nullable
	public static RuneColor byName(String name) {
		for(RuneColor color : RuneColor.values()) {
			if (color.getSerializedName().equals(name)) {
				return color;
			}
		}

		return null;
	}

	@Nullable
	public static RuneColor byDyeColor(DyeColor dyeColor) {
		for(RuneColor color : RuneColor.values()) {
			if (color.dyeColor == dyeColor) {
				return color;
			}
		}

		return null;
	}

}
