package org.violetmoon.zeta.client.config.widget;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.violetmoon.zeta.client.config.screen.ZetaScreen;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.Definition;

import java.util.function.Consumer;

public class DefaultDiscardDone {
	public final Button resetToDefault;
	public final Button discard;
	public final Button done;

	private final ZetaScreen screen;
	private final ChangeSet changes;
	private final Definition def;

	public DefaultDiscardDone(ZetaScreen screen, ChangeSet changes, Definition def) {
		int pad = 3;
		int bWidth = 121;
		int left = (screen.width - (bWidth + pad) * 3) / 2;
		int vStart = screen.height - 30;

		this.resetToDefault = new Button(left, vStart, bWidth, 20, Component.translatable("quark.gui.config.default"), this::resetToDefault);
		this.discard = new Button(left + bWidth + pad, vStart, bWidth, 20, Component.translatable("quark.gui.config.discard"), this::discard);
		this.done = new Button(left + (bWidth + pad) * 2, vStart, bWidth, 20, Component.translatable("gui.done"), this::done);

		this.screen = screen;
		this.changes = changes;
		this.def = def;
	}

	public void addWidgets(Consumer<AbstractWidget> addRenderableWidgets) {
		addRenderableWidgets.accept(resetToDefault);
		addRenderableWidgets.accept(discard);
		addRenderableWidgets.accept(done);
	}

	public void resetToDefault(Button b) {
		changes.resetToDefault(def);
	}

	public void discard(Button b) {
		changes.removeChange(def);
	}

	public void done(Button b) {
		screen.returnToParent();
	}
}
