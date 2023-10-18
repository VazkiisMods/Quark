package vazkii.quark.content_zeta;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.zeta.module.ModuleFinder;
import vazkii.zeta.module.ModuleSide;
import vazkii.zeta.module.ZetaLoadModuleAnnotationData;
import vazkii.zeta.module.ZetaModule;

//Based on ModuleFinder (quark's, not mine)
public class LegacyQuarkModuleFinder implements ModuleFinder {
	private static final Type LOAD_MODULE_TYPE = Type.getType(LoadModule.class);

	@Override
	public Stream<ZetaLoadModuleAnnotationData> get() {
		ModFileScanData scanData = ModList.get().getModFileById(Quark.MOD_ID).getFile().getScanResult();
		return scanData.getAnnotations().stream()
			.filter(annotationData -> LOAD_MODULE_TYPE.equals(annotationData.annotationType()))
			.sorted(Comparator.comparing(d -> d.getClass().getName()))
			.map(ad -> {
				Supplier<ZetaModule> constructor = () -> {
					try {
						Class<?> clazz = Class.forName(ad.clazz().getClassName(), false, LegacyQuarkModuleFinder.class.getClassLoader());
						QuarkModule cork = (QuarkModule) clazz.getConstructor().newInstance();

						//This concept doesn't exist in Zeta's module system, or it shouldn't anyway...
						//ideally Zeta will have real client modules
						Map<String, Object> vals = ad.annotationData();
						if(vals.containsKey("hasSubscriptions"))
							cork.hasSubscriptions = (boolean) vals.get("hasSubscriptions");
						if(vals.containsKey("subscribeOn")) {
							Set<Dist> subscribeTargets = EnumSet.noneOf(Dist.class);

							@SuppressWarnings("unchecked")
							List<ModAnnotation.EnumHolder> holders = (List<ModAnnotation.EnumHolder>) vals.get("subscribeOn");
							for (ModAnnotation.EnumHolder holder : holders)
								subscribeTargets.add(Dist.valueOf(holder.getValue()));

							cork.subscriptionTarget = Lists.newArrayList(subscribeTargets);
						}

						return cork;
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException("Exception creating QuarkModule (legacy)", e);
					}
				};

				String fullClassName = ad.clazz().getClassName();

				ModuleSide weird = ModuleSide.ANY; //Doesn't exist in Quark's annotation, quark doesnt have client-only modules
				return ZetaLoadModuleAnnotationData.fromForgeThing(constructor, fullClassName, ad.annotationData(), weird);
			});
	}
}
