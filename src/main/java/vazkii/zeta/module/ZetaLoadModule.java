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
	String category() default "";

	String name() default "";
	String description() default "";
	String[] antiOverlap() default {};

	//omitted: hasSubscriptions/subscribeOn

	boolean enabledByDefault() default true;

	//zeta extensions to LoadModule
	String clientExtensionOf() default "";
}
