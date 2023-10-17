package vazkii.zeta.module;

import java.util.Collection;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import vazkii.zeta.Zeta;

//this could also be implemented with ModFileScanData on Forge
public class ServiceLoaderModuleFinder implements Supplier<Collection<TentativeModule>> {
	public ServiceLoaderModuleFinder(Zeta z) {
		this.z = z;
	}

	private final Zeta z;

	public Collection<TentativeModule> get() {
		return ServiceLoader.load(ZetaModule.class)
			.stream()
			.map(provider -> {
				ZetaLoadModule annotation = provider.type().getAnnotation(ZetaLoadModule.class);
				if(annotation == null) {
					z.log.warn("Module class " + provider.type().getName() + " does not have a @ZetaLoadModule annotation. Skipping");
					return null;
				}
				return new ServiceLoaderBackedTentativeModule(provider, annotation);
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	record ServiceLoaderBackedTentativeModule(ServiceLoader.Provider<ZetaModule> provider, ZetaLoadModule annotation) implements TentativeModule {
		@Override
		public ZetaModule construct() {
			return provider.get();
		}

		@Override
		public Class<? extends ZetaModule> type() {
			return provider.type();
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
