package vazkii.zeta.module;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import vazkii.zeta.Zeta;

/**
 * TODO: other forms of module discovery and replacement (like a Forge-only module, or other types of 'replacement' modules)
 */
public class ZetaModuleManager {
	private final Zeta z;

	private final Map<String, ZetaModule> modulesById = new LinkedHashMap<>();

	public ZetaModuleManager(Zeta z) {
		this.z = z;
	}

	public @Nullable ZetaModule get(String id) {
		return modulesById.get(id);
	}

	public void load(Supplier<Collection<TentativeModule>> finder) {
		Collection<TentativeModule> tentative = finder.get();

		Collection<TentativeModule> toLoad = switch(z.getSide()) {
			case SERVER -> tentative.stream().filter(TentativeModule::isCommon).toList();
			case CLIENT -> {
				Map<String, TentativeModule> map = new LinkedHashMap<>();

				//fill the map with all common modules
				tentative.stream()
					.filter(TentativeModule::isCommon)
					.forEach(t -> map.put(t.id(), t));

				//if a module has a client component, load that one instead
				tentative.stream()
					.filter(TentativeModule::isClientOnly)
					.forEach(t -> {
						TentativeModule existing = map.get(t.clientExtensionOf());

						if(existing != null && existing.isCommon())
							map.put(t.clientExtensionOf(), t);
						else
							z.log.warn("illegal client module replacement: " + t + " replacing " + existing);
					});

				yield map.values();
			}
		};

		z.log.info("Discovered " + toLoad.size() + " modules to load");

		for(TentativeModule t : toLoad)
			modulesById.put(t.id(), construct(t));
	}

	private ZetaModule construct(TentativeModule t) {
		z.log.info("Constructing module " + t.id() + "...");

		ZetaModule module = t.construct();
		module.id = t.id();
		module.enabled = t.enabledByDefault() && t.antiOverlap().stream().noneMatch(z::isModLoaded);

		module.postConstruct();

		z.loadBus.subscribe(module.getClass());
		z.loadBus.subscribe(module);

		return module;
	}
}
