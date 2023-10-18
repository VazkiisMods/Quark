package vazkii.zeta.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Exists mainly because Forge ModFileScanData doesn't give you the annotation itself :S
 *
 * @see vazkii.zeta.module.ZetaLoadModule
 */
public record ZetaLoadModuleAnnotationData(
	Supplier<ZetaModule> constructor,
	String fullClassName,

	//and the rest is from ZetaLoadModule
	String category,
	ModuleSide side,
	String name,
	String description,
	String[] antiOverlap,
	boolean enabledByDefault,
	String clientReplacementOf
) {
	public static ZetaLoadModuleAnnotationData fromAnnotation(Supplier<ZetaModule> constructor, String fullClassName, ZetaLoadModule annotation) {
		return new ZetaLoadModuleAnnotationData(
			constructor,
			fullClassName,
			annotation.category(),
			annotation.side(),
			annotation.name(),
			annotation.description(),
			annotation.antiOverlap(),
			annotation.enabledByDefault(),
			annotation.clientReplacementOf()
		);
	}

	//clunky
	@SuppressWarnings("unchecked")
	public static ZetaLoadModuleAnnotationData fromForgeThing(Supplier<ZetaModule> constructor, String fullClassName, Map<String, Object> data, ModuleSide enumPls) {
		return new ZetaLoadModuleAnnotationData(
			constructor,
			fullClassName,
			(String) data.get("category"),
			enumPls,
			(String) data.getOrDefault("name", ""),
			(String) data.getOrDefault("description", ""),
			((List<String>) data.getOrDefault("antiOverlap", new ArrayList<String>())).toArray(new String[0]),
			(boolean) data.getOrDefault("enabledByDefault", true),
			(String) data.getOrDefault("clientReplacementOf", "")
		);
	}
}
