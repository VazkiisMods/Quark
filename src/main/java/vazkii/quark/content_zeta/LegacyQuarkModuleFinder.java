package vazkii.quark.content_zeta;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
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
import vazkii.zeta.module.TentativeModule;
import vazkii.zeta.module.ZetaModule;

//Based on ModuleFinder (quark's, not mine)
public class LegacyQuarkModuleFinder implements ModuleFinder {
	private static final Type LOAD_MODULE_TYPE = Type.getType(LoadModule.class);
	private static final Pattern MODULE_CLASS_PATTERN = Pattern.compile("vazkii\\.quark\\.(?:content|addons)\\.(\\w+)\\.module.\\w+Module");

	@Override
	public Stream<? extends TentativeModule> get() {
		ModFileScanData scanData = ModList.get().getModFileById(Quark.MOD_ID).getFile().getScanResult();
		return scanData.getAnnotations().stream()
			.filter(annotationData -> LOAD_MODULE_TYPE.equals(annotationData.annotationType()))
			.sorted(Comparator.comparing(d -> d.getClass().getName()))
			.map(LegacyAnnotationDataBackedTentativeModule::new);
	}

	public static class LegacyAnnotationDataBackedTentativeModule extends TentativeModule {
		private final ModFileScanData.AnnotationData ad;

		public LegacyAnnotationDataBackedTentativeModule(ModFileScanData.AnnotationData ad) {
			this.ad = ad;
		}

		@Override
		public ZetaModule construct() {
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
		}

		@Override
		protected String fullClassName() {
			return ad.clazz().getClassName();
		}

		@Override
		protected String rawCategory() {
			if(ad.annotationData().containsKey("category")) return ((ModAnnotation.EnumHolder) ad.annotationData().get("category")).getValue().toLowerCase(Locale.ROOT);
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
