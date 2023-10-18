package vazkii.zeta.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

/**
 * Exists mainly because Forge ModFileScanData doesn't give you the annotation itself :S
 *
 * @see vazkii.zeta.module.ZetaLoadModule
 */
public record ZetaLoadModuleAnnotationData(
	Class<? extends ZetaModule> clazz,

	//and the rest is from ZetaLoadModule
	String category,
	ModuleSide side,
	String name,
	String description,
	String[] antiOverlap,
	boolean enabledByDefault,
	@Nullable Class<? extends ZetaModule> clientReplacementOf,

	//TOOD: just emulating Quark's hasSubscriptions/subscribeOn to not totally kaboom the dedicated server yet
	@Deprecated boolean LEGACY_hasSubscriptions,
	@Deprecated List<Dist> LEGACY_subscribeOn
) {
	public static ZetaLoadModuleAnnotationData fromAnnotation(Class<? extends ZetaModule> clazz, ZetaLoadModule annotation) {
		return new ZetaLoadModuleAnnotationData(
			clazz,
			annotation.category(),
			annotation.side(),
			annotation.name(),
			annotation.description(),
			annotation.antiOverlap(),
			annotation.enabledByDefault(),
			annotation.clientReplacementOf(),
			false,
			List.of()
		);
	}

	//clunky
	@SuppressWarnings("unchecked")
	public static ZetaLoadModuleAnnotationData fromForgeThing(Class<? extends ZetaModule> clazz, Map<String, Object> data, ModuleSide enumPls) {
		return new ZetaLoadModuleAnnotationData(
			clazz,
			(String) data.get("category"),
			enumPls,
			(String) data.getOrDefault("name", ""),
			(String) data.getOrDefault("description", ""),
			((List<String>) data.getOrDefault("antiOverlap", new ArrayList<String>())).toArray(new String[0]),
			(boolean) data.getOrDefault("enabledByDefault", true),
			(Class<? extends ZetaModule>) resolveClassAugh((Type) data.getOrDefault("clientReplacementOf", null)),

			(boolean) data.getOrDefault("hasSubscriptions", false),
			data.containsKey("subscribeOn") ? List.of(Dist.CLIENT) : List.of(Dist.CLIENT, Dist.DEDICATED_SERVER)
		);
	}

	private static Class<?> resolveClassAugh(org.objectweb.asm.Type type) {
		if(type == null) return ZetaModule.class;
		try {
			return Class.forName(type.getClassName(), false, ZetaLoadModuleAnnotationData.class.getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("cant find class: " + type.getClassName(), e);
		}
	}
}
