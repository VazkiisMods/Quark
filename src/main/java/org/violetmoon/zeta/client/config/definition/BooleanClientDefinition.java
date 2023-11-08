package org.violetmoon.zeta.client.config.definition;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.widget.CheckboxButton;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.ValueDefinition;

import java.util.function.Consumer;

public class BooleanClientDefinition implements ClientDefinitionExt<ValueDefinition<Boolean>> {
	@Override
	public String getSubtitle(ChangeSet changes, ValueDefinition<Boolean> def) {
		return Boolean.toString(changes.get(def));
	}

	@Override
	public void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, ValueDefinition<Boolean> def, Consumer<AbstractWidget> widgets) {
		widgets.accept(new CheckboxButton(zc, 230, 3, changes, def));
	}
}
