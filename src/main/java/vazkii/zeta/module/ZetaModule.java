package vazkii.zeta.module;

import java.util.List;

import vazkii.quark.base.module.ModuleCategory;
import vazkii.zeta.Zeta;

/**
 * @see vazkii.quark.base.module.QuarkModule
 */
public class ZetaModule {
	//Need to move categories to a more flexible system than a hardcoded enum
	public ZetaCategory zetaCategory = null;
	@Deprecated public ModuleCategory category;

	public String displayName = "";
	public String lowercaseName = "";
	public String description = "";

	public List<String> antiOverlap = List.of();

	public boolean enabledByDefault = true;
	public boolean missingDep = false;

	//TODO: Can I delete some of these flags?
	protected boolean firstLoad = true;
	public boolean enabled = false;
	public boolean disabledByOverlap = false;
	public boolean configEnabled = false;
	public boolean ignoreAntiOverlap = false;

	public void postConstruct() {
		// NO-OP
	}

	//TODO: tidy
	public final void setEnabled(Zeta z, boolean willEnable) {
		configEnabled = willEnable;

		disabledByOverlap = false;
		if(missingDep)
			willEnable = false;
		else if(!ignoreAntiOverlap && antiOverlap != null && antiOverlap.stream().anyMatch(z::isModLoaded)) {
			disabledByOverlap = true;
			willEnable = false;
		}

		setEnabledAndManageSubscriptions(z, willEnable);
		firstLoad = false;
	}

	public final void setEnabledAndManageSubscriptions(Zeta z, boolean nowEnabled) {
		if(firstLoad || (this.enabled != nowEnabled)) {
			legacySub(nowEnabled);
			if(nowEnabled)
				z.playBus.subscribe(this.getClass()).subscribe(this);
			else
				z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
		}

		this.enabled = nowEnabled;
	}

	//Used in QuarkModule to add/remove from the Forge event bus
	@Deprecated
	protected void legacySub(boolean subscribing) { }
}
