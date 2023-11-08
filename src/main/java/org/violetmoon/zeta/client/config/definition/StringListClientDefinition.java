package org.violetmoon.zeta.client.config.definition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.screen.StringListInputScreen;
import org.violetmoon.zeta.client.config.widget.PencilButton;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.ValueDefinition;

import java.util.List;
import java.util.function.Consumer;

public class StringListClientDefinition implements ClientDefinitionExt<ValueDefinition<List<String>>> {
	@Override
	public String getSubtitle(ChangeSet changes, ValueDefinition<List<String>> def) {
		List<String> list = changes.get(def);

		if(list.isEmpty())
			return "[]";

		StringBuilder bob = new StringBuilder("[").append(list.get(0));
		for(int i = 1; i < list.size() && bob.length() < 30; i++)
			bob.append(", ").append(list.get(i));

		if(bob.length() > 30)
			return truncate(bob.toString());
		else
			return bob.append(']').toString();
	}

	@Override
	public void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, ValueDefinition<List<String>> def, Consumer<AbstractWidget> widgets) {
		Screen newScreen = new StringListInputScreen(zc, parent, changes, def);
		widgets.accept(new PencilButton(zc, 230, 3, b -> Minecraft.getInstance().setScreen(newScreen)));
	}
}
