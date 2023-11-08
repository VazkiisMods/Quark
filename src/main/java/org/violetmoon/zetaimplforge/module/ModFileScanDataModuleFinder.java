package org.violetmoon.zetaimplforge.module;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import org.violetmoon.zeta.module.ModuleFinder;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaLoadModuleAnnotationData;
import org.violetmoon.zeta.module.ZetaModule;

import java.util.stream.Stream;

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
					clazz = (Class<? extends ZetaModule>) Class.forName(ad.clazz().getClassName(), false, ModFileScanDataModuleFinder.class.getClassLoader());
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Exception getting QuarkModule (legacy)", e);
				}

				return ZetaLoadModuleAnnotationData.fromForgeThing(clazz, ad.annotationData());
			});
	}
}
