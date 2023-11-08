package org.violetmoon.zeta.module;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.load.ZModulesReady;
import org.violetmoon.zeta.util.ZetaSide;

/**
 * TODO: other forms of module discovery and replacement (like a Forge-only module, or other types of 'replacement' modules)
 */
public class ZetaModuleManager {
	private final Zeta z;

	private final Map<Class<? extends ZetaModule>, ZetaModule> modulesByKey = new LinkedHashMap<>();
	private final Map<String, ZetaCategory> categoriesById = new LinkedHashMap<>();
	private final Map<ZetaCategory, List<ZetaModule>> modulesInCategory = new HashMap<>();

	public ZetaModuleManager(Zeta z) {
		this.z = z;
	}

	// Modules //

	public Collection<ZetaModule> getModules() {
		return modulesByKey.values();
	}

	//SAFETY: check how TentativeModule.keyClass is assigned.
	// It's either set to the *same* class as the module implementation,
	// or set to the target of a clientReplacementOf operation, which is
	// checked to be assignableFrom the module implementation during loading.
	@SuppressWarnings("unchecked")
	public <M extends ZetaModule> M get(Class<M> keyClass) {
		return (M) modulesByKey.get(keyClass);
	}

	public <M extends ZetaModule> Optional<M> getOptional(Class<M> keyClass) {
		return Optional.ofNullable(get(keyClass));
	}

	public boolean isEnabled(Class<? extends ZetaModule> keyClass) {
		return get(keyClass).enabled;
	}
	
	public boolean isEnabledOrOverlapping(Class<? extends ZetaModule> keyClass) {
		ZetaModule module = get(keyClass);
		return module.enabled || module.disabledByOverlap;
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
			Map<Class<? extends ZetaModule>, TentativeModule> byClazz = new LinkedHashMap<>();

			//first, lay down all modules that are not client replacements
			for(TentativeModule tm : tentative)
				if(!tm.clientReplacement())
					byClazz.put(tm.clazz(), tm);

			//then overlay with the client replacements
			for(TentativeModule tm : tentative) {
				if(tm.clientReplacement()) {
					//SAFETY: already checked isAssignableFrom in TentativeModule
					@SuppressWarnings("unchecked")
					Class<? extends ZetaModule> superclass = (Class<? extends ZetaModule>) tm.clazz().getSuperclass();

					TentativeModule existing = byClazz.get(superclass);
					if(existing == null)
						throw new RuntimeException("Module " + tm.clazz().getName() + " wants to replace " + superclass.getName() + ", but that module isn't registered");

					byClazz.put(superclass, existing.replaceWith(tm));
				}
			}

			tentative = byClazz.values();
		}

		z.log.info("Discovered " + tentative.size() + " modules to load");

		for(TentativeModule t : tentative)
			modulesByKey.put(t.keyClass(), constructAndSetup(t));

		//Just for fun
		int count = modulesByKey.size();
		int nonLegacyCount = count - legacyModuleCount;
		String percentNonLegacy = String.format("%.2f", ((float) nonLegacyCount / count) * 100f);
		z.log.info("Constructed {} modules, of which {} are legacy @LoadModules. Module porting is {}% done.", modulesByKey.size(), legacyModuleCount, percentNonLegacy);

		z.loadBus.fire(new ZModulesReady());
	}

	int legacyModuleCount = 0;
	private ZetaModule constructAndSetup(TentativeModule t) {
		z.log.info("Constructing module {}...", t.displayName());

		//construct, set properties
		ZetaModule module = construct(t.clazz());

		module.zeta = z;
		module.category = t.category();

		module.displayName = t.displayName();
		module.lowercaseName = t.lowercaseName();
		module.description = t.description();

		module.antiOverlap = t.antiOverlap();

		module.enabledByDefault = t.enabledByDefault();

		//event busses
		module.setEnabled(z, t.enabledByDefault());
		z.loadBus.subscribe(module.getClass()).subscribe(module);

		//category upkeep
		modulesInCategory.computeIfAbsent(module.category, __ -> new ArrayList<>()).add(module);

		//post-construction callback
		module.postConstruct();

		return module;
	}

	private <Z extends ZetaModule> Z construct(Class<Z> clazz) {
		Z zeroArg = constructWithZeroArguments(clazz);
		if(zeroArg != null)
			return zeroArg;

		Z oneArg = constructWithOneArgument(clazz);
		if(oneArg != null)
			return oneArg;

		throw new RuntimeException("ZetaModule " + clazz.getName() + " should have a public zero or one-argument constructor");
	}

	private <Z extends ZetaModule> @Nullable Z constructWithZeroArguments(Class<Z> clazz) {
		try {
			Constructor<Z> cons = clazz.getConstructor();
			return cons.newInstance();
		} catch (NoSuchMethodException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException("Could not construct ZetaModule " + clazz.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private <Z extends ZetaModule> @Nullable Z constructWithOneArgument(Class<Z> clazz) {
		try {
			//find the one-argument constructor
			Optional<Constructor<?>> oneOpt = Arrays.stream(clazz.getConstructors())
				.filter(c -> c.getParameterCount() == 1)
				.findFirst();
			if(oneOpt.isEmpty())
				return null;
			Constructor<?> one = oneOpt.get();

			//find the argument type of that constructor
			Class<?> paramType = one.getParameters()[0].getType();

			//check it
			if(!ZetaModule.class.isAssignableFrom(paramType))
				throw new RuntimeException("ZetaModule " + clazz.getName() + " should take a ZetaModule as contructor parameter");
			Class<? extends ZetaModule> checkedParamType = (Class<? extends ZetaModule>) paramType;

			//construct it
			ZetaModule parent = construct(checkedParamType);

			//call my one-arg constructor with this module
			return (Z) one.newInstance(parent);
		} catch (Exception e) {
			throw new RuntimeException("Could not construct ZetaModule " + clazz.getName(), e);
		}
	}
}
