package vazkii.quark.base.module;

import java.util.Comparator;
import java.util.stream.Stream;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import vazkii.quark.base.Quark;
import vazkii.zeta.module.ModuleFinder;
import vazkii.zeta.module.ModuleSide;
import vazkii.zeta.module.ZetaLoadModuleAnnotationData;
import vazkii.zeta.module.ZetaModule;

//Based on ModuleFinder (quark's, not mine)
public class LegacyQuarkModuleFinder implements ModuleFinder {
	private static final Type LOAD_MODULE_TYPE = Type.getType(LoadModule.class);

	@SuppressWarnings("unchecked")
	@Override
	public Stream<ZetaLoadModuleAnnotationData> get() {
		ModFileScanData scanData = ModList.get().getModFileById(Quark.MOD_ID).getFile().getScanResult();
		return scanData.getAnnotations().stream()
			.filter(annotationData -> LOAD_MODULE_TYPE.equals(annotationData.annotationType()))
			.sorted(Comparator.comparing(d -> d.getClass().getName()))
			.map(ad -> {
				Class<? extends ZetaModule> clazz;
				try {
					clazz = (Class<? extends ZetaModule>) Class.forName(ad.clazz().getClassName(), false, LegacyQuarkModuleFinder.class.getClassLoader());
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Exception getting QuarkModule (legacy)", e);
				}

				//quark doesnt have client-only modules so just load everything on the server too
				//its hasSubscriptions/subscribeOn behavior is emulated to not break the dedi server for now
				return ZetaLoadModuleAnnotationData.fromForgeThing(clazz, ad.annotationData(), ModuleSide.ANY);
			});
	}
}
