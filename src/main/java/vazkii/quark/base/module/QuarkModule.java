package vazkii.quark.base.module;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.base.module.hint.HintManager;
import vazkii.quark.base.module.hint.HintObject;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZGatherHints;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.event.client.ZClientSetup;
import vazkii.zeta.module.ZetaModule;

import java.util.ArrayList;
import java.util.List;

public class QuarkModule extends ZetaModule {

	@Deprecated
	public boolean hasSubscriptions = false;
	@Deprecated
	public List<Dist> subscriptionTarget = Lists.newArrayList(Dist.CLIENT, Dist.DEDICATED_SERVER);

	private List<HintObject> annotationHints = new ArrayList<>();

	public QuarkModule() {
		// yep
	}

	//TODO: Push ZRegister into all 91 modules
	@Deprecated
	public void register() {
		// NO-OP
	}

	@LoadEvent
	public final void zRegister(ZRegister e) {
		register();
	}

	//TODO: Push ZConfigChanged into all 49 modules
	@Deprecated
	public void configChanged() {
		// NO-OP
	}

	@LoadEvent
	public final void zConfigChanged(ZConfigChanged e) {
		configChanged();
	}

	//TODO: ONLY used by AzaleaWoodModule, not even by the config
	@Deprecated
	public void enabledStatusChanged(boolean firstLoad, boolean oldStatus, boolean newStatus) {
		// NO-OP
	}

	//TODO: Push ZCommonSetup into all 37 modules
	@Deprecated
	public void setup() {
		// NO-OP
	}

	@LoadEvent
	public final void zSetup(ZCommonSetup e) {
		setup();
	}

	//TODO: Push ZClientSetup into all 28 modules
	@OnlyIn(Dist.CLIENT)
	@Deprecated
	public void clientSetup() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	@LoadEvent
	public final void zClientSetup(ZClientSetup e) {
		clientSetup();
	}

	public void pushFlags(ConfigFlagManager manager) {
		// NO-OP
	}

	@Override
	@Deprecated
	protected void legacySub(boolean subscribing) {
		if(hasSubscriptions && subscriptionTarget.contains(FMLEnvironment.dist)) {
			if(subscribing)
				MinecraftForge.EVENT_BUS.register(this);
			else
				MinecraftForge.EVENT_BUS.unregister(this);
		}
	}

	@PlayEvent
	public final void addAnnotationHints(ZGatherHints event) {
		if(annotationHints == null)
			annotationHints = HintManager.gatherHintAnnotations(ModuleLoader.INSTANCE.getConfigFlagManager(), this);

		for(HintObject hint : annotationHints)
			hint.apply(event);
	}
}
