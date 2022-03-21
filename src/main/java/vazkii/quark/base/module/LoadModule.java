package vazkii.quark.base.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.minecraftforge.api.distmarker.Dist;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadModule {

	ModuleCategory category();
	
	String name() default "";
	String description() default "";
	String[] antiOverlap() default { };
	String[] requiredMods() default { };

	boolean hasSubscriptions() default false;
	Dist[] subscribeOn() default { Dist.CLIENT, Dist.DEDICATED_SERVER };

	boolean enabledByDefault() default true;
	
}
