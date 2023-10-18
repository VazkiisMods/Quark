package vazkii.zeta.module;

import java.util.Set;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.Zeta;

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

			//TODO: Haxx for Quark legacy subscriptions, this entire thing (and actuallySubscribe) can be removed later
			boolean actuallySubscribe = true;
			if(this instanceof QuarkModule qm) {
				if(qm.hasSubscriptions && qm.subscriptionTarget.contains(FMLEnvironment.dist)) {
					if(nowEnabled)
						MinecraftForge.EVENT_BUS.register(this);
					else
						MinecraftForge.EVENT_BUS.unregister(this);
				}

				actuallySubscribe = qm.subscriptionTarget.contains(FMLEnvironment.dist);
			}

			if(actuallySubscribe) {
				if(nowEnabled)
					z.playBus.subscribe(this.getClass()).subscribe(this);
				else
					z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
			}
		}

		this.enabled = nowEnabled;
	}
}
