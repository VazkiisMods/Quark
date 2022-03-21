package vazkii.quark.base.module;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vazkii.quark.api.event.ModuleLoadedEvent;
import vazkii.quark.api.event.ModuleStateChangedEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.config.ConfigFlagManager;

import java.util.List;

public class QuarkModule {

	public ModuleCategory category = null;
	public String displayName = "";
	public String lowercaseName = "";
	public String description = "";
	public List<String> antiOverlap = null;
	public List<String> requiredMods = null;
	public boolean hasSubscriptions = false;
	public List<Dist> subscriptionTarget = Lists.newArrayList(Dist.CLIENT, Dist.DEDICATED_SERVER);
	public boolean enabledByDefault = true;
	public boolean missingDep = false;

	private boolean firstLoad = true;
	public boolean enabled = false;
	public boolean configEnabled = false;
	public boolean ignoreAntiOverlap = false;

	public QuarkModule() {
		// yep
	}

	public void construct() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void constructClient() {
		// NO-OP
	}

	public void register() {
		// NO-OP
	}

	public void postRegister() {
		// NO-OP
	}

	public void configChanged() {
		// NO-OP
	}

	public void enabledStatusChanged(boolean firstLoad, boolean oldStatus, boolean newStatus) {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void configChangedClient() {
		// NO-OP
	}

	public void setup() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void modelRegistry() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void modelBake(ModelBakeEvent event) {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void textureStitch(TextureStitchEvent.Pre event) {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void postTextureStitch(TextureStitchEvent.Post event) {
		// NO-OP
	}

	public void loadComplete() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void firstClientTick() {
		// NO-OP
	}

	public void pushFlags(ConfigFlagManager manager) {
		// NO-OP
	}

	protected void enqueue(Runnable r) {
		ModuleLoader.INSTANCE.enqueue(r);
	}

	public final void setEnabled(boolean enabled) {
		configEnabled = enabled;
		if(firstLoad) {
			Quark.LOG.info("Loading Module " + displayName);
			MinecraftForge.EVENT_BUS.post(new ModuleLoadedEvent(lowercaseName));
		}

		if(requiredMods != null) {
			ModList list = ModList.get();
			for(String s : requiredMods)
				if(!list.isLoaded(s)) {
					missingDep = true;
					break;
				}
		}

		if(missingDep)
			enabled = false;
		else if(!ignoreAntiOverlap && antiOverlap != null) {
			ModList list = ModList.get();
			for(String s : antiOverlap)
				if(list.isLoaded(s)) {
					enabled = false;
					break;
				}
		}

		setEnabledAndManageSubscriptions(firstLoad, enabled);
		firstLoad = false;
	}

	private void setEnabledAndManageSubscriptions(boolean firstLoad, boolean enabled) {
		if(MinecraftForge.EVENT_BUS.post(new ModuleStateChangedEvent(lowercaseName, enabled)))
			enabled = false;

		boolean wasEnabled = this.enabled;
		this.enabled = enabled;

		boolean changed = wasEnabled != enabled;

		if(changed) {
			if(hasSubscriptions && subscriptionTarget.contains(FMLEnvironment.dist)) {
				if(enabled)
					MinecraftForge.EVENT_BUS.register(this);
				else if(!firstLoad)
					MinecraftForge.EVENT_BUS.unregister(this);
			}

			enabledStatusChanged(firstLoad, wasEnabled, enabled);
		}
	}

}
