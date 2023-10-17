package vazkii.zeta.module;

import java.util.Objects;
import java.util.ServiceLoader;
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

	@Override
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

	public static class ServiceLoaderBackedTentativeModule extends TentativeModule {
		private final ServiceLoader.Provider<ZetaModule> provider;
		private final ZetaLoadModule annotation;

		public ServiceLoaderBackedTentativeModule(ServiceLoader.Provider<ZetaModule> provider, ZetaLoadModule annotation) {
			this.provider = provider;
			this.annotation = annotation;
		}

		@Override
		public ZetaModule construct() {
			return provider.get();
		}

		@Override
		protected String fullClassName() {
			return provider.type().getName();
		}

		@Override
		public String rawCategory() {
			return annotation.category();
		}

		@Override
		public String rawName() {
			return annotation.name();
		}

		@Override
		public String rawDescription() {
			return annotation.description();
		}

		@Override
		public String[] rawAntiOverlap() {
			return annotation.antiOverlap();
		}

		@Override
		public boolean rawEnabledByDefault() {
			return annotation.enabledByDefault();
		}

		@Override
		public String rawClientExtensionOf() {
			return annotation.clientExtensionOf();
		}
	}
}
