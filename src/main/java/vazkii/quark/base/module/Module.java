package vazkii.quark.base.module;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.quark.api.event.ModuleLoadedEvent;
import vazkii.quark.api.event.ModuleStateChangedEvent;
import vazkii.quark.base.Quark;

public class Module {

	public String displayName = "";
	public String lowercaseName = "";
	public String description = "";
	public List<String> antiOverlap = null;
	public boolean hasSubscriptions = false;
	public List<Dist> subscriptionTarget = Lists.newArrayList(Dist.CLIENT, Dist.DEDICATED_SERVER);
	public boolean enabledByDefault = true;
	
	private boolean firstLoad = true;
	public boolean enabled = false;
	public boolean ignoreAntiOverlap = false;

	public void construct() {
		// NO-OP
	}

	public void modulesStarted() {
		// NO-OP
	}
	
	public void buildConfigSpec(ForgeConfigSpec.Builder builder, List<Runnable> callbacks) {
		// NO-OP
	}
	
	public void configChanged() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void configChangedClient() {
		// NO-OP
	}
	
	public void earlySetup() {
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
	public void textureStitch(TextureStitchEvent.Pre event) {
		// NO-OP
	}

	public void loadComplete() {
		// NO-OP
	}
	
	public void pushFlags(ConfigFlagManager manager) {
		// NO-OP
	}
	
	public final void setEnabled(boolean enabled) {
		if(firstLoad) {
			Quark.LOG.info("Loading Module " + displayName);
			MinecraftForge.EVENT_BUS.post(new ModuleLoadedEvent(lowercaseName));
		}
		firstLoad = false;
		
		if(!ignoreAntiOverlap && antiOverlap != null) {
			ModList list = ModList.get();
			for(String s : antiOverlap)
				if(list.isLoaded(s))
					return;
		}
		
		setEnabledAndManageSubscriptions(enabled);
	}
	
	private void setEnabledAndManageSubscriptions(boolean enabled) {
		if(MinecraftForge.EVENT_BUS.post(new ModuleStateChangedEvent(lowercaseName, enabled)))
			enabled = false;
		
		boolean wasEnabled = this.enabled;
		this.enabled = enabled;
		
		if(hasSubscriptions && subscriptionTarget.contains(FMLEnvironment.dist) && wasEnabled != enabled) {
			if(enabled)
				MinecraftForge.EVENT_BUS.register(this);
			else MinecraftForge.EVENT_BUS.unregister(this);
		}
	}
	
}
