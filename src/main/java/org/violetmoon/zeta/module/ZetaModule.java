package org.violetmoon.zeta.module;

import java.util.List;
import java.util.Set;

import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.loading.ZGatherHints;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ZetaModule {
	public Zeta zeta;
	public ZetaCategory category;

	public String displayName = "";
	public String lowercaseName = "";
	public String description = "";

	public Set<String> antiOverlap = Set.of();

	public boolean enabled = false;
	public boolean enabledByDefault = false;
	public boolean disabledByOverlap = false;
	public boolean ignoreAntiOverlap = false;

	public void postConstruct() {
		// NO-OP
	}

	public final void setEnabled(Zeta z, boolean willEnable) {
		//TODO: is this the right approach for handling category enablement :woozy_face:
		if(z.configManager != null && !z.configManager.isCategoryEnabled(category))
			willEnable = false;

		if(category != null && !category.requiredModsLoaded(z))
			willEnable = false;

		if(!ignoreAntiOverlap && antiOverlap.stream().anyMatch(z::isModLoaded)) {
			disabledByOverlap = true;
			willEnable = false;
		} else
			disabledByOverlap = false;

		setEnabledAndManageSubscriptions(z, willEnable);
	}

	private void setEnabledAndManageSubscriptions(Zeta z, boolean nowEnabled) {
		if(this.enabled == nowEnabled)
			return;
		this.enabled = nowEnabled;

		if(nowEnabled)
			z.playBus.subscribe(this.getClass()).subscribe(this);
		else
			z.playBus.unsubscribe(this.getClass()).unsubscribe(this);
	}

	@PlayEvent
	public final void addAnnotationHints(ZGatherHints event) {
		event.gatherHintsFromModule(this, zeta.configManager.getConfigFlagManager());
	}
}
