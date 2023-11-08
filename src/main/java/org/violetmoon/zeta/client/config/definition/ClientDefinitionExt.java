package org.violetmoon.zeta.client.config.definition;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.Definition;
import org.violetmoon.zeta.config.SectionDefinition;

import java.util.function.Consumer;

public interface ClientDefinitionExt<T extends Definition> {
	default String getGuiDisplayName(ChangeSet changes, T def) {
		String defName = def instanceof SectionDefinition ? def.name.replace("_", "") : def.name;
		String transKey = "quark.config." + String.join(".", def.path) + "." + def.name.toLowerCase().replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "") + ".name";

		String localized = I18n.get(transKey);
		if(localized.isEmpty() || localized.equals(transKey))
			return defName;

		return localized;
	}

	String getSubtitle(ChangeSet changes, T def);

	void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, T def, Consumer<AbstractWidget> widgets);

	default String truncate(String in) {
		if(in.length() > 30)
			return in.substring(0, 27) + "...";
		else
			return in;
	}

	class Default implements ClientDefinitionExt<Definition> {
		@Override
		public String getSubtitle(ChangeSet changes, Definition def) {
			return "";
		}

		@Override
		public void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, Definition def, Consumer<AbstractWidget> widgets) {

		}
	}
}
