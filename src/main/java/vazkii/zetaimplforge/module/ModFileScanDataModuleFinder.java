package vazkii.zetaimplforge.module;

import java.util.stream.Stream;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import vazkii.quark.base.module.LegacyQuarkModuleFinder;
import vazkii.zeta.module.ModuleFinder;
import vazkii.zeta.module.ModuleSide;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaLoadModuleAnnotationData;
import vazkii.zeta.module.ZetaModule;

public class ModFileScanDataModuleFinder implements ModuleFinder {
	private static final Type ZLM_TYPE = Type.getType(ZetaLoadModule.class);
	private final ModFileScanData mfsd;

	public ModFileScanDataModuleFinder(ModFileScanData mfsd) {
		this.mfsd = mfsd;
	}

	public ModFileScanDataModuleFinder(String modid) {
		this(ModList.get().getModFileById(modid).getFile().getScanResult());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<ZetaLoadModuleAnnotationData> get() {
		return mfsd.getAnnotations().stream()
			.filter(ad -> ad.annotationType().equals(ZLM_TYPE))
			.map(ad -> {
				Class<? extends ZetaModule> clazz;
				try {
					clazz = (Class<? extends ZetaModule>) Class.forName(ad.clazz().getClassName(), false, LegacyQuarkModuleFinder.class.getClassLoader());
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Exception getting QuarkModule (legacy)", e);
				}

				ModAnnotation.EnumHolder pls = (ModAnnotation.EnumHolder) ad.annotationData().get("side");
				ModuleSide weird = pls == null ? ModuleSide.ANY : ModuleSide.valueOf(pls.getValue());

				return ZetaLoadModuleAnnotationData.fromForgeThing(clazz, ad.annotationData(), weird);
			});
	}
}
