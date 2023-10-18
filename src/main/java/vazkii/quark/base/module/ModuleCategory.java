package vazkii.quark.base.module;

/**
 * @deprecated I'm in the process of migrating everything to ZetaCategory, which is more freeform.
 *             The only reason this enum still exists is cause I'd have to fix all the modules ;0
 *             Category icons & requirements are set when configuring the ZetaModuleManager.
 *             In Quark this is done in CommonProxy.
 */
@Deprecated
public enum ModuleCategory {
	AUTOMATION,
	BUILDING,
	MANAGEMENT,
	TOOLS,
	TWEAKS,
	WORLD,
	MOBS,
	CLIENT,
	EXPERIMENTAL,
	ODDITIES
}
