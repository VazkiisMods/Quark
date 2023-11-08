package org.violetmoon.zeta.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.config.ConfigFlagManager;
import org.violetmoon.quark.base.handler.GeneralConfig;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.play.loading.ZGatherAdditionalFlags;
import org.violetmoon.zeta.module.ZetaCategory;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.module.ZetaModuleManager;

public class ConfigManager {
	private final Zeta z;
	private final ConfigFlagManager cfm;
	private final SectionDefinition rootConfig;

	//for updating the values of @Config annotations to match the current state of the config
	// and other "listening for config load" purposes
	private final List<Consumer<IZetaConfigInternals>> databindings = new ArrayList<>();
	private Consumer<IZetaConfigInternals> onConfigReloadJEI;

	//ummmmmmm i think my abstraction isn't very good
	private final @Nullable SectionDefinition generalSection;
	private final Map<ZetaCategory, SectionDefinition> categoriesToSections = new HashMap<>();

	private final Map<ZetaCategory, ValueDefinition<Boolean>> categoryEnabledOptions = new HashMap<>();
	private final Map<ZetaModule, ValueDefinition<Boolean>> ignoreAntiOverlapOptions = new HashMap<>();
	private final Map<ZetaModule, ValueDefinition<Boolean>> moduleEnabledOptions = new HashMap<>();

	//state
	private final Set<ZetaCategory> enabledCategories = new HashSet<>();

	public ConfigManager(Zeta z, Object rootPojo) {
		this.z = z;
		this.cfm = new ConfigFlagManager(z);

		//TODO: where to put this lol
		z.loadBus.subscribe(rootPojo).subscribe(rootPojo.getClass());
		z.playBus.subscribe(rootPojo).subscribe(rootPojo.getClass());

		ZetaModuleManager modules = z.modules;

		//all modules are enabled by default
		enabledCategories.addAll(modules.getCategories());

		this.rootConfig = new SectionDefinition("root", List.of());

		if(rootPojo == null)
			generalSection = null;
		else {
			generalSection = rootConfig.getOrCreateSubsection("general", List.of());
			ConfigObjectMapper.readInto(generalSection, rootPojo, databindings, cfm);
		}

		for(ZetaCategory category : modules.getInhabitedCategories()) {
			//category enablement option
			categoryEnabledOptions.put(category, rootConfig.getOrCreateSubsection("categories", List.of()).addValue(category.name, List.of(), true));

			//per-category options:
			SectionDefinition categorySection = rootConfig.getOrCreateSubsection(category.name, List.of());
			categoriesToSections.put(category, categorySection);

			for(ZetaModule module : modules.modulesInCategory(category)) {
				//module flag
				cfm.putModuleFlag(module);

				//module enablement option
				moduleEnabledOptions.put(module, categorySection.addValue(module.displayName, List.of(module.description), module.enabledByDefault));

				//module @Config options
				SectionDefinition moduleSection = categorySection.getOrCreateSubsection(module.lowercaseName, List.of(module.description));
				ConfigObjectMapper.readInto(moduleSection, module, databindings, cfm);

				//anti overlap
				if(!module.antiOverlap.isEmpty()) {
					List<String> antiOverlapComment = new ArrayList<>(module.antiOverlap.size() + 3);
					antiOverlapComment.add("This feature disables itself if any of the following mods are loaded:");
					for (String s : module.antiOverlap)
						antiOverlapComment.add(" - " + s);
					antiOverlapComment.add("This is done to prevent content overlap.");
					antiOverlapComment.add("You can turn this on to force the feature to be loaded even if the above mods are also loaded.");

					ignoreAntiOverlapOptions.put(module, moduleSection.addValue("Ignore Anti Overlap", antiOverlapComment, false));
				}
			}
		}

		//update extra flags
		z.playBus.fire(new ZGatherAdditionalFlags(cfm));

		//managing module enablement in one go
		//adding this to the *start* of the list so modules are enabled before anything else runs
		//Its Janky !
		databindings.add(0, i -> {
			categoryEnabledOptions.forEach((category, option) -> setCategoryEnabled(category, i.get(option)));
			ignoreAntiOverlapOptions.forEach((module, option) -> module.ignoreAntiOverlap = !GeneralConfig.useAntiOverlap || i.get(option));
			moduleEnabledOptions.forEach((module, option) -> {
				setModuleEnabled(module, i.get(option));
				cfm.putModuleFlag(module);
			});

			//update extra flags
			z.playBus.fire(new ZGatherAdditionalFlags(cfm));
		});

		rootConfig.trimEmptySubsections();
	}

	public SectionDefinition getRootConfig() {
		return rootConfig;
	}

	// mapping between internal and external representations of the config (?)

	public @Nullable SectionDefinition getGeneralSection() {
		return generalSection;
	}

	public SectionDefinition getCategorySection(ZetaCategory cat) {
		return categoriesToSections.get(cat);
	}

	public ValueDefinition<Boolean> getCategoryEnabledOption(ZetaCategory cat) {
		return categoryEnabledOptions.get(cat);
	}

	public ValueDefinition<Boolean> getModuleEnabledOption(ZetaModule module) {
		return moduleEnabledOptions.get(module);
	}

	// support for the options added by this class

	private void setCategoryEnabled(ZetaCategory cat, boolean enabled) {
		if(enabled)
			enabledCategories.add(cat);
		else
			enabledCategories.remove(cat);

		//TODO TODO bad bad bad, just forcing setEnabled to rerun since it checks category enablement
		for(ZetaModule mod : z.modules.modulesInCategory(cat)) {
			mod.setEnabled(z, mod.enabled);
		}
	}

	private void setModuleEnabled(ZetaModule module, boolean enabled) {
		module.setEnabled(z, enabled);
	}

	public boolean isCategoryEnabled(ZetaCategory cat) {
		return enabledCategories.contains(cat);
	}

	// ummm

	public ConfigFlagManager getConfigFlagManager() {
		return cfm;
	}

	public void onReload() {
		IZetaConfigInternals internals = z.configInternals;
		databindings.forEach(c -> c.accept(internals));

		if(onConfigReloadJEI != null)
			onConfigReloadJEI.accept(internals);
	}

	public void setJeiReloadListener(Consumer<IZetaConfigInternals> consumer) {
		this.onConfigReloadJEI = consumer;
		consumer.accept(z.configInternals); //run it now as well
	}
}
