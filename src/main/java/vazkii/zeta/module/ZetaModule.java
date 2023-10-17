package vazkii.zeta.module;

import vazkii.zeta.Zeta;

/**
 * @see vazkii.quark.base.module.QuarkModule
 */
public class ZetaModule {
	public String id = "";

	private boolean firstLoad = true;
	public boolean enabled = true;

	public void postConstruct() {
		// NO-OP
	}

	public final void setEnabledAndManageSubscriptions(Zeta z, boolean nowEnabled) {
		if(firstLoad || (this.enabled != nowEnabled)) {
			if(nowEnabled)
				z.playBus.subscribe(this.getClass()).subscribe(this);
			else
				z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
		}

		this.enabled = nowEnabled;
		firstLoad = false;
	}
}
