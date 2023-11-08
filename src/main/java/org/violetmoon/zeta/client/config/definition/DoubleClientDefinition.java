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

public class DoubleClientDefinition implements ClientDefinitionExt<ValueDefinition<Double>> {
	@Override
	public String getSubtitle(ChangeSet changes, ValueDefinition<Double> def) {
		return Double.toString(changes.get(def));
	}

	@Override
	public void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, ValueDefinition<Double> def, Consumer<AbstractWidget> widgets) {
		Screen newScreen = new AbstractEditBoxInputScreen<>(zc, parent, changes, def) {
			@Override
			protected int maxStringLength() {
				return 16;
			}

			@Override
			protected @Nullable Double fromString(String string) {
				try {
					return Double.parseDouble(string);
				} catch (Exception e) {
					return null;
				}
			}
		};
		widgets.accept(new PencilButton(zc, 230, 3, b -> Minecraft.getInstance().setScreen(newScreen)));
	}
}
