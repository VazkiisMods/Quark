package vazkii.quark.base.module;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.config.ModConfig;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.CreativeTabHandler;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.base.module.config.ConfigResolver;
import vazkii.zeta.module.TentativeModule;
import vazkii.zeta.module.ZetaModule;

import java.util.ArrayList;
import java.util.List;

//TODO ZETA: im in the process of stripping this class for parts
@Deprecated
public final class ModuleLoader {

	private enum Step {
		POST_REGISTER
	}

	public static final ModuleLoader INSTANCE = new ModuleLoader();

	private final List<Step> stepsHandled = new ArrayList<>();

	private ConfigResolver config;
	private Runnable onConfigReloadJEI;

	private ModuleLoader() { }

	public void start() {
		config = new ConfigResolver();
		config.makeSpec();
	}

	public ModConfig getConfig() {
		return config.getConfig();
	}

	//TODO ZETA: bad hack that allows separating hints from the rest of the module system
	public ConfigFlagManager getConfigFlagManager() {
		return config.flagManager;
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
	}

	@Deprecated
	public boolean isModuleEnabled(Class<? extends ZetaModule> moduleClazz) {
		ZetaModule module = Quark.ZETA.modules.get(moduleClazz);
		return module != null && module.enabled;
	}

	@Deprecated
	public boolean isModuleEnabledOrOverlapping(Class<? extends ZetaModule> moduleClazz) {
		ZetaModule module = Quark.ZETA.modules.get(moduleClazz);
		return module != null && (module.enabled || module.disabledByOverlap);
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
