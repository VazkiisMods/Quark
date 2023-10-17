package vazkii.zeta.module;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

import vazkii.zeta.Zeta;

/**
 * @see vazkii.zetaimplforge.module.ModFileScanDataModuleFinder alternative Forge-only implementation
 */
public class ServiceLoaderModuleFinder implements ModuleFinder {
	public ServiceLoaderModuleFinder(Zeta z) {
		this.z = z;
	}

	private final Zeta z;

	public Stream<? extends TentativeModule> get() {
		return ServiceLoader.load(ZetaModule.class)
			.stream()
			.map(provider -> {
				ZetaLoadModule annotation = provider.type().getAnnotation(ZetaLoadModule.class);
				if(annotation == null) {
					z.log.warn("Module class " + provider.type().getName() + " was found through ServiceLoader, but does not have a @ZetaLoadModule annotation. Skipping");
					return null;
				}
				return new ServiceLoaderBackedTentativeModule(provider, annotation);
			})
			.filter(Objects::nonNull);
	}

	record ServiceLoaderBackedTentativeModule(ServiceLoader.Provider<ZetaModule> provider, ZetaLoadModule annotation) implements TentativeModule {
		@Override
		public ZetaModule construct() {
			return provider.get();
		}

		@Override
		public String id() {
			return annotation.id();
		}

		@Override
		public String clientExtensionOf() {
			return annotation.clientExtensionOf();
		}

		@Override
		public boolean enabledByDefault() {
			return annotation.enabledByDefault();
		}

		@Override
		public Set<String> antiOverlap() {
			return Set.of(annotation.antiOverlap());
		}
	}
}
