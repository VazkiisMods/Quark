package vazkii.zetaimplforge.module;

import java.util.List;
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

	public static class AnnotationDataBackedTentativeModule extends TentativeModule {
		private final ModFileScanData.AnnotationData ad;

		public AnnotationDataBackedTentativeModule(ModFileScanData.AnnotationData ad) {
			this.ad = ad;
		}

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
		protected String fullClassName() {
			return ad.clazz().getClassName();
		}

		@Override
		protected String rawCategory() {
			if(ad.annotationData().containsKey("category")) return (String) ad.annotationData().get("category");
			else return "";
		}

		@Override
		protected String rawName() {
			if(ad.annotationData().containsKey("name")) return (String) ad.annotationData().get("name");
			else return "";
		}

		@Override
		protected String rawDescription() {
			if(ad.annotationData().containsKey("description")) return (String) ad.annotationData().get("description");
			else return "";
		}

		@SuppressWarnings("unchecked")
		@Override
		protected String[] rawAntiOverlap() {
			if(ad.annotationData().containsKey("antiOverlap")) return ((List<String>) ad.annotationData().get("antiOverlap")).toArray(String[]::new);
			else return new String[0];
		}

		@Override
		protected boolean rawEnabledByDefault() {
			if(ad.annotationData().containsKey("enabledByDefault")) return (Boolean) ad.annotationData().get("enabledByDefault");
			else return true;
		}

		@Override
		protected String rawClientExtensionOf() {
			if(ad.annotationData().containsKey("clientExtensionOf")) return (String) ad.annotationData().get("clientExtensionOf");
			else return "";
		}
	}
}
