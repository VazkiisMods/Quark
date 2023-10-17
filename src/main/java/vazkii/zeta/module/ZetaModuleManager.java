package vazkii.zeta.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.Zeta;
import vazkii.zeta.event.ZModulesReady;

/**
 * TODO: other forms of module discovery and replacement (like a Forge-only module, or other types of 'replacement' modules)
 */
public class ZetaModuleManager {
	private final Zeta z;

	private final Map<String, ZetaModule> modulesById = new LinkedHashMap<>();
	private final Map<String, ZetaCategory> categoriesById = new LinkedHashMap<>();
	private final Map<ZetaCategory, List<ZetaModule>> modulesInCategory = new HashMap<>();

	public ZetaModuleManager(Zeta z) {
		this.z = z;
	}

	// Modules //

	public @Nullable ZetaModule get(String lowercaseName) {
		return modulesById.get(lowercaseName);
	}

	public Collection<ZetaModule> getModules() {
		return modulesById.values();
	}

	// Categories //

	public ZetaCategory getCategory(String id) {
		return categoriesById.computeIfAbsent(id, ZetaCategory::unknownCategory);
	}

	public Collection<ZetaCategory> getCategories() {
		return categoriesById.values();
	}

	public List<ZetaModule> modulesInCategory(ZetaCategory cat) {
		return modulesInCategory.get(cat);
	}

	// Loading //

	//first call this
	public void initCategories(Iterable<ZetaCategory> cats) {
		for(ZetaCategory cat : cats) categoriesById.put(cat.id(), cat);
	}

	//then call this
	public void load(ModuleFinder finder) {
		Collection<? extends TentativeModule> tentative = finder.get()
			.peek(t -> t.derive(this::getCategory))
			.sorted(Comparator.comparing(t -> t.displayName))
			.toList();

		Collection<? extends TentativeModule> toLoad = switch(z.getSide()) {
			case SERVER -> tentative.stream().filter(TentativeModule::isCommon).toList();
			case CLIENT -> {
				Map<String, TentativeModule> map = new LinkedHashMap<>();

				//fill the map with all common modules
				tentative.stream()
					.filter(TentativeModule::isCommon)
					.forEach(t -> map.put(t.lowercaseName, t));

				//if a module has a client component, load that one instead
				tentative.stream()
					.filter(TentativeModule::isClientOnly)
					.forEach(t -> {
						TentativeModule existing = map.get(t.clientExtensionOf);

						if(existing != null && existing.isCommon())
							map.put(t.clientExtensionOf, t);
						else
							z.log.warn("illegal client module replacement: " + t + " replacing " + existing);
					});

				yield map.values();
			}
		};

		z.log.info("Discovered " + toLoad.size() + " modules to load");

		for(TentativeModule t : toLoad)
			modulesById.put(t.lowercaseName, construct(t));

		z.loadBus.fire(new ZModulesReady());
	}

	private ZetaModule construct(TentativeModule t) {
		z.log.info("Constructing module " + t.displayName + "...");

		//construct, set properties
		ZetaModule module = t.construct();

		module.zetaCategory = t.category;
		module.category = module.zetaCategory.legacy();

		module.displayName = t.displayName;
		module.lowercaseName = t.lowercaseName;
		module.description = t.description;

		module.antiOverlap = t.antiOverlap.stream().toList(); //TODO make it a Set

		module.enabledByDefault = t.enabledByDefault;
		module.missingDep = !t.category.modsLoaded(z);

		//event busses
		module.setEnabled(z, t.enabledByDefault);
		z.loadBus.subscribe(module.getClass()).subscribe(module);

		//category upkeep
		modulesInCategory.computeIfAbsent(module.zetaCategory, __ -> new ArrayList<>()).add(module);
		if(module instanceof QuarkModule qm) module.category.addModule(qm); //TODO LEGACY config

		//post-construction callback
		module.postConstruct();

		return module;
	}
}
