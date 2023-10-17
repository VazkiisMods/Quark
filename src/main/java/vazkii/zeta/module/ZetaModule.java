package vazkii.zeta.module;

import vazkii.zeta.Zeta;

/**
 * @see vazkii.quark.base.module.QuarkModule
 */
public class ZetaModule {
	public String id = "";

	public boolean enabled = true;

	public void postConstruct() {
		// NO-OP
	}

	public final void setEnabledAndManageSubscriptions(Zeta z, boolean nowEnabled) {
		boolean wasEnabled = enabled;
		this.enabled = nowEnabled;

		if(wasEnabled != nowEnabled) {
			if(enabled)
				z.playBus.subscribe(this.getClass()).subscribe(this);
			else
				z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
		}
	}
}
