package org.violetmoon.zeta.client.config.definition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.screen.AbstractEditBoxInputScreen;
import org.violetmoon.zeta.client.config.widget.PencilButton;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.ValueDefinition;

import java.util.function.Consumer;

public class StringClientDefinition implements ClientDefinitionExt<ValueDefinition<String>> {
	@Override
	public String getSubtitle(ChangeSet changes, ValueDefinition<String> def) {
		return truncate(changes.get(def));
	}

	@Override
	public void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, ValueDefinition<String> def, Consumer<AbstractWidget> widgets) {
		Screen newScreen = new AbstractEditBoxInputScreen<>(zc, parent, changes, def) {
			@Override
			protected @Nullable String fromString(String string) {
				return string;
			}
		};
		widgets.accept(new PencilButton(zc, 230, 3, b -> Minecraft.getInstance().setScreen(newScreen)));
	}
}
