package org.violetmoon.quark.addons.oddities.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.content.tools.base.RuneColor;

import java.util.List;
import java.util.Locale;

public record TinyPotatoInfo(@Nullable RuneColor runeColor, boolean enchanted, String name) {

	private static final List<String> RAINBOW_NAMES = List.of("gay homosexual", "rainbow", "lgbt", "lgbtq", "lgbtq+", "gay");
	private static final List<String> ENCHANTMENT_NAMES = List.of("enchanted", "glowy", "shiny", "gay");

	private static boolean matches(String name, String match) {
		return name.equals(match) || name.startsWith(match + " ");
	}

	private static String removeFromFront(String name, String match) {
		return name.substring(match.length()).trim();
	}

	public static TinyPotatoInfo fromComponent(Component component) {
		return fromString(component.getString());
	}

	public static TinyPotatoInfo fromString(String string) {
		string = ChatFormatting.stripFormatting(string);

		if(string == null)
			return new TinyPotatoInfo(null, false, "");

		string = string.trim().toLowerCase(Locale.ROOT);

		boolean enchanted = false;
		for(String enchant : ENCHANTMENT_NAMES) {
			if(matches(string, enchant)) {
				enchanted = true;
				string = removeFromFront(string, enchant);
				break;
			}
		}

		RuneColor color = null;

		if(enchanted) {
			for(RuneColor runeColor : RuneColor.values()) {
				String key = runeColor.getName().replace("_", " ");
				if(matches(string, key)) {
					color = runeColor;
					string = removeFromFront(string, key);
					break;
				} else if (key.contains("gray")) {
					key = key.replace("gray", "grey");
					if(matches(string, key)) {
						color = runeColor;
						string = removeFromFront(string, key);
						break;
					}
				}
			}

			if(color == null) {
				for(String rainbow : RAINBOW_NAMES) {
					if(matches(string, rainbow)) {
						color = RuneColor.RAINBOW;
						string = removeFromFront(string, rainbow);
						break;
					}
				}
			}
		}

		return new TinyPotatoInfo(color, enchanted, string);
	}
}
