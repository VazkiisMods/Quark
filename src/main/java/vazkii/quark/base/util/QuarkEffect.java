/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [03/07/2016, 17:24:22 (GMT)]
 */
package vazkii.quark.base.util;

import net.minecraft.core.Registry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import vazkii.quark.base.Quark;

public class QuarkEffect extends MobEffect {

	public QuarkEffect(String name, MobEffectCategory type, int color) {
		super(type, color);

		Quark.ZETA.registry.register(this, name, Registry.MOB_EFFECT_REGISTRY);
	}
}
