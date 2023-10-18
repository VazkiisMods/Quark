package vazkii.zeta.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;

/**
 * Exists mainly because Forge ModFileScanData doesn't give you the annotation itself :S
 *
 * @see vazkii.zeta.module.ZetaLoadModule
 */
public record ZetaLoadModuleAnnotationData(
	Class<?> clazz,

	//and the rest is from ZetaLoadModule
	String category,
	ModuleSide side,
	String name,
	String description,
	String[] antiOverlap,
	boolean enabledByDefault,
	boolean clientReplacement,

	//TOOD: just emulating Quark's hasSubscriptions/subscribeOn to not totally kaboom the dedicated server yet
	@Deprecated boolean LEGACY_hasSubscriptions,
	@Deprecated List<Dist> LEGACY_subscribeOn
) {
	public static ZetaLoadModuleAnnotationData fromAnnotation(Class<?> clazz, ZetaLoadModule annotation) {
		return new ZetaLoadModuleAnnotationData(
			clazz,
			annotation.category(),
			annotation.side(),
			annotation.name(),
			annotation.description(),
			annotation.antiOverlap(),
			annotation.enabledByDefault(),
			annotation.clientReplacement(),
			false,
			List.of()
		);
	}

	//clunky
	@SuppressWarnings("unchecked")
	public static ZetaLoadModuleAnnotationData fromForgeThing(Class<?> clazz, Map<String, Object> data, ModuleSide enumPls) {
		return new ZetaLoadModuleAnnotationData(
			clazz,
			(String) data.get("category"),
			enumPls,
			(String) data.getOrDefault("name", ""),
			(String) data.getOrDefault("description", ""),
			((List<String>) data.getOrDefault("antiOverlap", new ArrayList<String>())).toArray(new String[0]),
			(boolean) data.getOrDefault("enabledByDefault", true),
			(boolean) data.getOrDefault("clientReplacement", false),

			(boolean) data.getOrDefault("hasSubscriptions", false),
			data.containsKey("subscribeOn") ? List.of(Dist.CLIENT) : List.of(Dist.CLIENT, Dist.DEDICATED_SERVER)
		);
	}
}
