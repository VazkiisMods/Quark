package org.violetmoon.zeta.recipe;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

// Copy of Forge ICondition
public interface IZetaCondition {

	ResourceLocation getID();
	boolean test(IContext context);

	//TODO: do we need this
	interface IContext {
		default <T> Collection<Holder<T>> getTag(TagKey<T> key) {
			return getAllTags(key.registry()).getOrDefault(key.location(), Set.of());
		}

		<T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry);
	}
}
