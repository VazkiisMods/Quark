package vazkii.zeta.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
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

	//TODO ZETA (Very important): move this state to some sort of "config" area
	// It's only here since it's stored *on* the category *enum* in current Quark
	@Deprecated
	private final Set<ZetaCategory> MOVE_TO_CONFIG_enabledCategoires = new HashSet<>();

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
		if(id == null || id.isEmpty()) id = "Unknown";

		return categoriesById.computeIfAbsent(id, ZetaCategory::unknownCategory);
	}

	public Collection<ZetaCategory> getCategories() {
		return categoriesById.values();
	}

	public List<ZetaCategory> getInhabitedCategories() {
		return categoriesById.values().stream()
			.filter(c -> !modulesInCategory(c).isEmpty())
			.toList();
	}

	public List<ZetaModule> modulesInCategory(ZetaCategory cat) {
		return modulesInCategory.computeIfAbsent(cat, __ -> new ArrayList<>());
	}

	@Deprecated
	public boolean MOVE_TO_CONFIG_categoryIsEnabled(ZetaCategory cat) {
		return MOVE_TO_CONFIG_enabledCategoires.contains(cat);
	}

	@Deprecated
	public void MOVE_TO_CONFIG_setCategoryEnabled(ZetaCategory cat, boolean enabled) {
		if(enabled)
			MOVE_TO_CONFIG_enabledCategoires.add(cat);
		else
			MOVE_TO_CONFIG_enabledCategoires.remove(cat);
	}

	// Loading //

	//first call this
	public void initCategories(Iterable<ZetaCategory> cats) {
		for(ZetaCategory cat : cats) categoriesById.put(cat.name, cat);
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

		module.category = t.category;

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
		modulesInCategory.computeIfAbsent(module.category, __ -> new ArrayList<>()).add(module);

		//post-construction callback
		module.postConstruct();

		return module;
	}
}
