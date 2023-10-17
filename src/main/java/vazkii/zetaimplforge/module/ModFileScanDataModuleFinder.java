package vazkii.zetaimplforge.module;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import vazkii.zeta.module.ModuleFinder;
import vazkii.zeta.module.TentativeModule;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zetaimplforge.ForgeZeta;

public class ModFileScanDataModuleFinder implements ModuleFinder {
	private static final Type ZLM_TYPE = Type.getType(ZetaLoadModule.class);
	private final ModFileScanData mfsd;

	public ModFileScanDataModuleFinder(ModFileScanData mfsd) {
		this.mfsd = mfsd;
	}

	public ModFileScanDataModuleFinder(String modid) {
		this(ModList.get().getModFileById(modid).getFile().getScanResult());
	}

	@Override
	public Stream<? extends TentativeModule> get() {
		return mfsd.getAnnotations().stream()
			.filter(ad -> ad.annotationType().equals(ZLM_TYPE))
			.map(AnnotationDataBackedTentativeModule::new);
	}

	record AnnotationDataBackedTentativeModule(ModFileScanData.AnnotationData ad) implements TentativeModule {
		@Override
		public ZetaModule construct() {
			try {
				Class<?> clazz = Class.forName(ad.clazz().getClassName(), false, ForgeZeta.class.getClassLoader());
				return (ZetaModule) clazz.getConstructor().newInstance();
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Exception creating ZetaModule", e);
			}
		}

		@Override
		public String id() {
			if(ad.annotationData().containsKey("id")) return (String) ad.annotationData().get("id");
			else return "";
		}

		@Override
		public String clientExtensionOf() {
			if(ad.annotationData().containsKey("clientExtensionOf")) return (String) ad.annotationData().get("clientExtensionOf");
			else return "";
		}

		@Override
		public boolean enabledByDefault() {
			if(ad.annotationData().containsKey("enabledByDefault")) return (Boolean) ad.annotationData().get("enabledByDefault");
			else return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Set<String> antiOverlap() {
			if(ad.annotationData().containsKey("antiOverlap")) return new HashSet<>((List<String>) ad.annotationData().get("antiOverlap"));
			else return Set.of();
		}
	}
}
