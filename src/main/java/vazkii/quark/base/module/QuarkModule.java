package vazkii.quark.base.module;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.zeta.module.ZetaModule;

import java.util.List;

public class QuarkModule extends ZetaModule {

	@Deprecated
	public boolean hasSubscriptions = false;
	@Deprecated
	public List<Dist> subscriptionTarget = Lists.newArrayList(Dist.CLIENT, Dist.DEDICATED_SERVER);

	public QuarkModule() {
		// yep
	}

	public void pushFlags(ConfigFlagManager manager) {
		// NO-OP
	}

	@Override
	protected boolean legacySubscribe(boolean nowEnabled) {
		if(hasSubscriptions && subscriptionTarget.contains(FMLEnvironment.dist)) {
			if(nowEnabled)
				MinecraftForge.EVENT_BUS.register(this);
			else
				MinecraftForge.EVENT_BUS.unregister(this);
		}

		return subscriptionTarget.contains(FMLEnvironment.dist);
	}
}
