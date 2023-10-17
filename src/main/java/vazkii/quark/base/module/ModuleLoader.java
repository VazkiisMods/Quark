package vazkii.quark.base.module;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.config.ModConfig;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.config.ConfigResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//TODO ZETA: im in the process of stripping this class for parts
public final class ModuleLoader {

	private enum Step {
		POST_REGISTER, GENERATE_HINTS
	}

	public static final ModuleLoader INSTANCE = new ModuleLoader();

	private Map<Class<? extends QuarkModule>, QuarkModule> foundModules = new HashMap<>();
	private final List<Step> stepsHandled = new ArrayList<>();

	private ConfigResolver config;
	private Runnable onConfigReloadJEI;

	private ModuleLoader() { }

	public void start() {
		Quark.ZETA.modules.getModules().forEach(module -> {
			if(module instanceof QuarkModule qm) foundModules.put(qm.getClass(), qm);
		});

		config = new ConfigResolver();
		config.makeSpec();
	}

	public ModConfig getConfig() {
		return config.getConfig();
	}

	public void register() {
		stepsHandled.add(Step.POST_REGISTER);
		CreativeTabHandler.finalizeTabs();
		config.registerConfigBoundElements();
	}

	public void configChanged() {
		if(!stepsHandled.contains(Step.POST_REGISTER))
			return; // We don't want to mess with changing config values before objects are registered

		if (onConfigReloadJEI != null)
			onConfigReloadJEI.run();
		config.configChanged();
		//dispatch(Step.CONFIG_CHANGED, QuarkModule::configChanged);
	}

	public void setup() {
		Quark.proxy.handleQuarkConfigChange();
	}

	//TODO: this could be some sort of GatherHintsEvent, fired on the play bus (because of that enabled() check)
	// Stick one in QuarkModule that scans its own class for @Hint annotations too or something
	public void addStackInfo(BiConsumer<Item, Component> consumer) {
		dispatch(Step.GENERATE_HINTS, m -> {
			if(m.enabled)
				m.addStackInfo(consumer);
		});
	}

	private void dispatch(Step step, Consumer<QuarkModule> run) {
		Quark.LOG.info("Dispatching Module Step " + step);
		foundModules.values().forEach(run);
		stepsHandled.add(step);
	}

	public boolean isModuleEnabled(Class<? extends QuarkModule> moduleClazz) {
		QuarkModule module = getModuleInstance(moduleClazz);
		return module != null && module.enabled;
	}

	public boolean isModuleEnabledOrOverlapping(Class<? extends QuarkModule> moduleClazz) {
		QuarkModule module = getModuleInstance(moduleClazz);
		return module != null && (module.enabled || module.disabledByOverlap);
	}

	public QuarkModule getModuleInstance(Class<? extends QuarkModule> moduleClazz) {
		return foundModules.get(moduleClazz);
	}

	public boolean isItemEnabled(Item i) {
		if(i instanceof IQuarkItem qi) {
			return qi.isEnabled();
		}
		else if(i instanceof BlockItem bi) {
			Block b = bi.getBlock();
			if(b instanceof IQuarkBlock qb) {
				return qb.isEnabled();
			}
		}

		return true;
	}

	/**
	 * Meant only to be called internally.
	 */
	public void initJEICompat(Runnable jeiRunnable) {
		onConfigReloadJEI = jeiRunnable;
		onConfigReloadJEI.run();
	}

}
