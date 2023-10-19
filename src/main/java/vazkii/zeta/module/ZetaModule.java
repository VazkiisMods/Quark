package vazkii.zeta.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.hint.HintManager;
import vazkii.quark.base.module.hint.HintObject;
import vazkii.zeta.Zeta;
import vazkii.zeta.event.ZGatherHints;
import vazkii.zeta.event.bus.PlayEvent;

/**
 * @see vazkii.quark.base.module.QuarkModule
 */
public class ZetaModule {
	public ZetaCategory category = null;

	public String displayName = "";
	public String lowercaseName = "";
	public String description = "";

	public Set<String> antiOverlap = Set.of();

	public boolean enabledByDefault = true;
	public boolean missingDep = false;

	//TODO: Can I delete some of these flags? (maybe some sort of "DisabledReason"?)
	// The config should probably be the source of *truth* for this state, but having flags here *is* convenient
	private boolean firstLoad = true;
	public boolean enabled = false;
	public boolean disabledByOverlap = false;
	public boolean configEnabled = false;
	public boolean ignoreAntiOverlap = false;

	private List<HintObject> annotationHints = null;

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

			//TODO: Haxx for QuarkModule's @SubscribeEvent and sidedness
			boolean actuallySubscribe = legacySubscribe(nowEnabled);
			if(actuallySubscribe) {
				if(nowEnabled)
					z.playBus.subscribe(this.getClass()).subscribe(this);
				else
					z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
			}
		}

		this.enabled = nowEnabled;
	}

	@Deprecated
	protected boolean legacySubscribe(boolean nowEnabled) {
		return true;
	}

	@PlayEvent
	public final void addAnnotationHints(ZGatherHints event) {
		if(annotationHints == null)
			annotationHints = HintManager.gatherHintAnnotations(ModuleLoader.INSTANCE.getConfigFlagManager(), this);

		for(HintObject hint : annotationHints)
			hint.apply(event);
	}
}
