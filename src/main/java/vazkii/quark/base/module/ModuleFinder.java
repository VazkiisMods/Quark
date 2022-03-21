package vazkii.quark.base.module;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.apache.commons.lang3.text.WordUtils;
import org.objectweb.asm.Type;
import vazkii.quark.base.Quark;

import java.util.*;

public final class ModuleFinder {

	private static final Type LOAD_MODULE_TYPE = Type.getType(LoadModule.class);

	private final Map<Class<? extends QuarkModule>, QuarkModule> foundModules = new HashMap<>();

	public void findModules() {
		ModFileScanData scanData = ModList.get().getModFileById(Quark.MOD_ID).getFile().getScanResult();
		scanData.getAnnotations().stream()
				.filter(annotationData -> LOAD_MODULE_TYPE.equals(annotationData.annotationType()))
				.sorted(Comparator.comparing(d -> d.getClass().getName()))
				.forEach(this::loadModule);
	}

	@SuppressWarnings("unchecked")
	private void loadModule(AnnotationData target) {
		try {
			Type type = target.clazz();
			Class<?> clazz = Class.forName(type.getClassName(), false, Quark.class.getClassLoader());
			QuarkModule moduleObj = (QuarkModule) clazz.getDeclaredConstructor().newInstance();

			Map<String, Object> vals = target.annotationData();
			ModuleCategory category = getOrMakeCategory((ModAnnotation.EnumHolder) vals.get("category"));

			if(category.isAddon()) {
				String mod = category.requiredMod;
				if(mod != null && !mod.isEmpty() && !ModList.get().isLoaded(mod))
					moduleObj.missingDep = true;
			}

			if(vals.containsKey("name"))
				moduleObj.displayName = (String) vals.get("name");
			else
				moduleObj.displayName = WordUtils.capitalizeFully(clazz.getSimpleName().replaceAll("Module$", "").replaceAll("(?<=.)([A-Z])", " $1"));
			moduleObj.lowercaseName = moduleObj.displayName.toLowerCase(Locale.ROOT).replaceAll(" ", "_");

			if(vals.containsKey("description"))
				moduleObj.description = (String) vals.get("description");

			if(vals.containsKey("antiOverlap"))
				moduleObj.antiOverlap = (List<String>) vals.get("antiOverlap");

			if(vals.containsKey("requiredMods"))
				moduleObj.requiredMods = (List<String>) vals.get("requiredMods");

			if(vals.containsKey("hasSubscriptions"))
				moduleObj.hasSubscriptions = (boolean) vals.get("hasSubscriptions");

			if(vals.containsKey("subscribeOn")) {
				Set<Dist> subscribeTargets = EnumSet.noneOf(Dist.class);

				List<ModAnnotation.EnumHolder> holders = (List<ModAnnotation.EnumHolder>) vals.get("subscribeOn");
				for (ModAnnotation.EnumHolder holder : holders)
					subscribeTargets.add(Dist.valueOf(holder.getValue()));

				moduleObj.subscriptionTarget = Lists.newArrayList(subscribeTargets);
			}

			if(vals.containsKey("enabledByDefault"))
				moduleObj.enabledByDefault = (Boolean) vals.get("enabledByDefault");

			category.addModule(moduleObj);
			moduleObj.category = category;

			foundModules.put((Class<? extends QuarkModule>) clazz, moduleObj);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException("Failed to load Module " + target, e);
		}
	}

	private ModuleCategory getOrMakeCategory(ModAnnotation.EnumHolder category) {
		return ModuleCategory.valueOf(category.getValue());
	}

	public Map<Class<? extends QuarkModule>, QuarkModule> getFoundModules() {
		return foundModules;
	}

}
