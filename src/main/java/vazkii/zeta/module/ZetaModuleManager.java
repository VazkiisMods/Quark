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
import vazkii.zeta.util.ZetaSide;

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
		Collection<TentativeModule> tentative = finder.get()
			.map(data -> TentativeModule.from(data, this::getCategory))
			.filter(tm -> tm.appliesTo(z.side))
			.sorted(Comparator.comparing(TentativeModule::displayName))
			.toList();

		//this is the part where we handle "client replacement" modules !!
		if(z.side == ZetaSide.CLIENT) {
			Map<String, TentativeModule> byName = new LinkedHashMap<>();

			//first, lay down all modules that are not client replacements
			for(TentativeModule tm : tentative)
				if(tm.clientReplacementOf() == null)
					byName.put(tm.lowercaseName(), tm);

			//then overlay with the client replacements
			for(TentativeModule tm : tentative)
				if(tm.clientReplacementOf() != null)
					byName.put(tm.clientReplacementOf(), tm);

			tentative = byName.values();
		}

		z.log.info("Discovered " + tentative.size() + " modules to load");

		for(TentativeModule t : tentative)
			modulesById.put(t.lowercaseName(), construct(t));

		z.loadBus.fire(new ZModulesReady());
	}

	private ZetaModule construct(TentativeModule t) {
		z.log.info("Constructing module " + t.displayName() + "...");

		//construct, set properties
		ZetaModule module = t.constructor().get();

		module.category = t.category();

		module.displayName = t.displayName();
		module.lowercaseName = t.lowercaseName();
		module.description = t.description();

		module.antiOverlap = t.antiOverlap().stream().toList(); //TODO make it a Set

		module.enabledByDefault = t.enabledByDefault();
		module.missingDep = !t.category().modsLoaded(z);

		//event busses
		module.setEnabled(z, t.enabledByDefault());
		z.loadBus.subscribe(module.getClass()).subscribe(module);

		//category upkeep
		modulesInCategory.computeIfAbsent(module.category, __ -> new ArrayList<>()).add(module);

		//post-construction callback
		module.postConstruct();

		return module;
	}
}
