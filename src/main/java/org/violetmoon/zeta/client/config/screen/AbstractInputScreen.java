package org.violetmoon.zeta.client.config.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.definition.ClientDefinitionExt;
import org.violetmoon.zeta.client.config.widget.DefaultDiscardDone;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.ValueDefinition;

//TODO: assumes the thing your editing has a direct ValueDefinition, which isn't true for certain types of config screens
// like, RGBA color inputs actually correspond to SectionDefinitions in the config
public abstract class AbstractInputScreen<T> extends ZetaScreen {
	protected final ChangeSet changes;
	protected final ValueDefinition<T> def;
	protected final ClientDefinitionExt<ValueDefinition<T>> ext;

	protected DefaultDiscardDone defaultDiscardDone;

	public AbstractInputScreen(ZetaClient zc, Screen parent, ChangeSet changes, ValueDefinition<T> def) {
		super(zc, parent);
		this.changes = changes;
		this.def = def;
		this.ext = zc.clientConfigManager.getExt(def);
	}

	protected T get() {
		return changes.get(def);
	}

	protected void set(T thing) {
		changes.set(def, thing);
	}

	protected abstract void forceUpdateWidgetsTo(T value);

	@Override
	protected void init() {
		super.init();

		defaultDiscardDone = new DefaultDiscardDone(this, changes, def) {
			@Override
			public void resetToDefault(Button b) {
				super.resetToDefault(b);
				forceUpdateWidgetsTo(get());
			}

			@Override
			public void discard(Button b) {
				super.discard(b);
				forceUpdateWidgetsTo(get());
			}
		};
		defaultDiscardDone.addWidgets(this::addRenderableWidget);

		updateButtonStatus(true);
	}

	//call changes.set() first, so isDirty will return an up-to-date value
	protected void updateButtonStatus(boolean valid) {
		defaultDiscardDone.done.active = valid;
		defaultDiscardDone.discard.active = !valid || changes.isDirty(def);
	}
}
