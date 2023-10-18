package vazkii.zeta.module;

import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import vazkii.zeta.util.ZetaSide;

/**
 * performs some common data-munging of the data straight off a ZetaLoadModule annotation
 */
public record TentativeModule(
	Supplier<ZetaModule> constructor,

	ZetaCategory category,
	ModuleSide side,
	String displayName,
	String lowercaseName,
	String description,
	Set<String> antiOverlap,
	boolean enabledByDefault,

	@Nullable String clientReplacementOf
) {
	public static TentativeModule from(ZetaLoadModuleAnnotationData data, Function<String, ZetaCategory> categoryResolver) {
		String simpleName = data.fullClassName();
		simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1);

		String displayName;
		if(data.name().isEmpty())
			displayName = WordUtils.capitalizeFully(simpleName.replaceAll("Module$", "").replaceAll("(?<=.)([A-Z])", " $1"));
		else
			displayName = data.name();

		String lowercaseName = displayName.toLowerCase(Locale.ROOT).replace(" ", "_");

		String clientReplacementOf = data.clientReplacementOf().trim();
		if(clientReplacementOf.isEmpty())
			clientReplacementOf = null;

		return new TentativeModule(
			data.constructor(),
			categoryResolver.apply(data.category()),
			data.side(),
			displayName,
			lowercaseName,
			data.description(),
			Set.of(data.antiOverlap()),
			data.enabledByDefault(),
			clientReplacementOf
		);
	}

	public boolean appliesTo(ZetaSide side) {
		return this.side.appliesTo(side);
	}
}
