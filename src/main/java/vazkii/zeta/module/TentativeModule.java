package vazkii.zeta.module;

import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.text.WordUtils;

//TODO: this sucks lol
public abstract class TentativeModule {
	public abstract ZetaModule construct();
	protected abstract String fullClassName();

	protected abstract String rawCategory();
	protected abstract String rawName();
	protected abstract String rawDescription();
	protected abstract String[] rawAntiOverlap();
	protected abstract boolean rawEnabledByDefault();
	protected abstract String rawClientExtensionOf();

	public ZetaCategory category;

	public String displayName;
	public String lowercaseName; //"id"

	public String description;
	public Set<String> antiOverlap;
	public boolean enabledByDefault;

	String clientExtensionOf;

	void derive(Function<String, ZetaCategory> categoryGetter) {
		category = categoryGetter.apply(rawCategory());

		String simpleName = fullClassName();
		simpleName = simpleName.substring(simpleName.lastIndexOf('.') + 1);

		if(rawName().isEmpty())
			displayName = WordUtils.capitalizeFully(simpleName.replaceAll("Module$", "").replaceAll("(?<=.)([A-Z])", " $1"));
		else
			displayName = rawName();

		lowercaseName = displayName.toLowerCase(Locale.ROOT).replace(" ", "_");

		description = rawDescription();
		antiOverlap = Set.of(rawAntiOverlap());
		enabledByDefault = rawEnabledByDefault();

		clientExtensionOf = rawClientExtensionOf();
	}

	public boolean isCommon() {
		return clientExtensionOf.isEmpty();
	}

	public boolean isClientOnly() {
		return !isCommon();
	}
}
