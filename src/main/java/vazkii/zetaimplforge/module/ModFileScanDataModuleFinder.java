package vazkii.zetaimplforge.module;

import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import vazkii.zeta.module.ModuleFinder;
import vazkii.zeta.module.ModuleSide;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaLoadModuleAnnotationData;
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
	public Stream<ZetaLoadModuleAnnotationData> get() {
		return mfsd.getAnnotations().stream()
			.filter(ad -> ad.annotationType().equals(ZLM_TYPE))
			.map(ad -> {
				Supplier<ZetaModule> constructor = () -> {
					try {
						Class<?> clazz = Class.forName(ad.clazz().getClassName(), false, ForgeZeta.class.getClassLoader());
						return (ZetaModule) clazz.getConstructor().newInstance();
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException("Exception creating ZetaModule", e);
					}
				};

				String fullClassName = ad.clazz().getClassName();

				ModAnnotation.EnumHolder pls = (ModAnnotation.EnumHolder) ad.annotationData().get("side");
				ModuleSide weird = pls == null ? ModuleSide.ANY : ModuleSide.valueOf(pls.getValue());

				return ZetaLoadModuleAnnotationData.fromForgeThing(constructor, fullClassName, ad.annotationData(), weird);
			});
	}
}
