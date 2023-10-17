package vazkii.quark.base.module;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLEnvironment;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.base.module.hint.HintObject;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientModulesReady;
import vazkii.zeta.event.client.ZClientSetup;
import vazkii.zeta.event.client.ZConfigChangedClient;
import vazkii.zeta.event.client.ZRegisterReloadListeners;
import vazkii.zeta.module.ZetaModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class QuarkModule extends ZetaModule {

	@Deprecated
	public boolean hasSubscriptions = false;
	@Deprecated
	public List<Dist> subscriptionTarget = Lists.newArrayList(Dist.CLIENT, Dist.DEDICATED_SERVER);

	public List<HintObject> hints = new ArrayList<>(); //TODO move to ZetaModule maybe

	public QuarkModule() {
		// yep
	}

	@Deprecated //QuarkModule
	public void construct() {
		// NO-OP
	}

	@Override public final void postConstruct() { construct(); }

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void constructClient() {
		// NO-OP
	}

	@LoadEvent public final void zConstructClient(ZClientModulesReady e) { constructClient(); }

	@Deprecated //QuarkModule
	public void register() {
		// NO-OP
	}

	@LoadEvent public final void zRegister(ZRegister e) { register(); }

	@Deprecated //QuarkModule
	public void postRegister() {
		// NO-OP
	}

	@LoadEvent public final void zRegisterPost(ZRegister.Post e) { postRegister(); }

	@Deprecated //QuarkModule
	public void configChanged() {
		// NO-OP
	}

	@LoadEvent public final void zConfigChanged(ZConfigChanged e) { configChanged(); }

	@Deprecated //QuarkModule
	public void enabledStatusChanged(boolean firstLoad, boolean oldStatus, boolean newStatus) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void configChangedClient() {
		// NO-OP
	}

	@LoadEvent public final void zConfigChangedClient(ZConfigChangedClient e) { configChangedClient(); }

	@Deprecated //QuarkModule, also use the one that takes enqueueWork
	public void setup() {
		// NO-OP
	}

	@Deprecated //QuarkModule
	public void setup(Consumer<Runnable> enqueueWork) {
		setup();
	}

	@LoadEvent public final void zSetup(ZCommonSetup e) { setup(e::enqueueWork); }

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void registerReloadListeners(Consumer<PreparableReloadListener> manager) {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	@LoadEvent public final void zRegisterReloadListener(ZRegisterReloadListeners e) { registerReloadListeners(e); }

	@OnlyIn(Dist.CLIENT)
	@Deprecated //QuarkModule, also use the one that takes enqueueWork
	public void clientSetup() {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void clientSetup(Consumer<Runnable> enqueueWork) {
		clientSetup();
	}

	@OnlyIn(Dist.CLIENT)
	@LoadEvent public final void zClientSetup(ZClientSetup e) { clientSetup(e::enqueueWork); }

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void modelBake(ModelEvent.BakingCompleted event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void modelLayers(EntityRenderersEvent.AddLayers event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void textureStitch(TextureStitchEvent.Pre event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void postTextureStitch(TextureStitchEvent.Post event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void registerKeybinds(RegisterKeyMappingsEvent event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void registerItemColors(RegisterColorHandlersEvent.Item event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		// NO-OP
	}

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void registerClientTooltipComponentFactories(RegisterClientTooltipComponentFactoriesEvent event) {
		// NO-OP
	}

	@Deprecated //QuarkModule, also use the one that takes enqueueWork
	public void loadComplete() {
		// NO-OP
	}

	@Deprecated //QuarkModule
	public void loadComplete(Consumer<Runnable> enqueueWork) {
		loadComplete();
	}

	// <HINTS>
	public final void addStackInfo(BiConsumer<Item, Component> consumer) {
		if(!enabled)
			return;

		for(HintObject hint : hints)
			hint.apply(consumer);
		addAdditionalHints(consumer);
	}

	public void addAdditionalHints(BiConsumer<Item, Component> consumer) {

	}
	// </HINTS>

	@Deprecated //QuarkModule
	@OnlyIn(Dist.CLIENT)
	public void firstClientTick() {
		// NO-OP
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
}
