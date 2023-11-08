package org.violetmoon.zeta.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.base.config.type.IConfigType;

/**
 * Common superclass of a... "thing" in a config definition (a value or section).
 *
 * @see org.violetmoon.zeta.config.SectionDefinition
 * @see org.violetmoon.zeta.config.ValueDefinition
 */
public abstract class Definition {
	public final String name;
	public final List<String> comment;
	public final @Nullable SectionDefinition parent;
	public final Collection<String> path;

	//TODO: awkward, set from ConfigObjectMapper
	// Treat this as immutable and for reference only !!!!
	public @Nullable Object hint;

	public Definition(String name, List<String> comment, @Nullable SectionDefinition parent) {
		this.name = name;
		this.parent = parent;

		//TODO lol
		this.comment = comment.stream()
			.flatMap(s -> Arrays.stream(s.split("\n")))
			.filter(line -> !line.trim().isEmpty()).collect(Collectors.toList());

		if(parent == null)
			path = Collections.emptyList(); //TODO: skipping the "root" SectionDefinition
		else {
			path = new ArrayList<>(parent.path);
			path.add(name);
			((ArrayList<?>) path).trimToSize();
		}
	}

	public String[] commentToArray() {
		return comment == null ? new String[0] : comment.toArray(new String[0]);
	}

	public String commentToString() {
		return comment == null ? "" : String.join("\n", comment);
	}

	//TODO: weird, should probably be moved to GUI code - note this is SHARED code so i cant directly use i18n
	public final String getGuiDisplayName(Function<String, String> i18nDotGet) {
		String defName = this instanceof SectionDefinition ? name.replace("_", "") : name;
		String transKey = "quark.config." + String.join(".", path) + "." + name.toLowerCase().replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "") + ".name";

		String localized = i18nDotGet.apply(transKey);
		if(localized.isEmpty() || localized.equals(transKey))
			return defName;

		return localized;
	}
}
