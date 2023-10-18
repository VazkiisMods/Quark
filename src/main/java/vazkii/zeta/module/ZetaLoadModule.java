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
	/**
	 * The name of the category this module belongs to.
	 * See ZetaModuleManager.addCategories.
	 */
	String category() default "";

	/**
	 * Which physical side this module will be loaded on.
	 */
	ModuleSide side() default ModuleSide.ANY;

	/**
	 * The name of this module. If unspecified, defaults to de-camelcasing the module's class name.
	 * Ex "MyCoolModule"'s name defaults to "My Cool Module".
	 */
	String name() default "";
	String description() default "";

	/**
	 * Mod IDs that, if present, this module will disable itself by default on.
	 */
	String[] antiOverlap() default {};

	//omitted: hasSubscriptions/subscribeOn

	boolean enabledByDefault() default true;

	/**
	 * If "t
	 */
	boolean clientReplacement() default false;
}
