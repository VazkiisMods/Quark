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
	public Stream<ZetaLoadModuleAnnotationData> get() {
		return ServiceLoader.load(ZetaModule.class)
			.stream()
			.map(provider -> {
				ZetaLoadModule annotation = provider.type().getAnnotation(ZetaLoadModule.class);
				if(annotation == null) {
					z.log.warn("Module class " + provider.type().getName() + " was found through ServiceLoader, but does not have a @ZetaLoadModule annotation. Skipping");
					return null;
				}

				return ZetaLoadModuleAnnotationData.fromAnnotation(provider.type(), annotation);
			})
			.filter(Objects::nonNull);
	}
}
