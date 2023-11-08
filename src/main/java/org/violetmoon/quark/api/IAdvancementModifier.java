package org.violetmoon.quark.api;

import com.google.common.base.Supplier;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public interface IAdvancementModifier {

	Set<ResourceLocation> getTargets();
	boolean apply(ResourceLocation res, IMutableAdvancement adv);


	default IAdvancementModifier setCondition(Supplier<Boolean> cond){
		return this;
	}

	default boolean isActive(){
		return true;
	}

}
