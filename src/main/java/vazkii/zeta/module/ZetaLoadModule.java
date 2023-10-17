package vazkii.zeta.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see vazkii.quark.base.module.LoadModule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ZetaLoadModule {
	String id(); //TODO: make it optional and default to simpleName? or something
	String clientExtensionOf() default "";

	boolean enabledByDefault() default true;
	String[] antiOverlap() default {};
}
