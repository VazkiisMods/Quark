package org.violetmoon.zeta.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RegistryUtil {
	
	public static <T> List<T> massRegistryGet(Collection<String> coll, Registry<T> reg) {
		return coll.stream().map(ResourceLocation::new).map(reg::get).filter(Objects::nonNull).toList();
	}
	
}
