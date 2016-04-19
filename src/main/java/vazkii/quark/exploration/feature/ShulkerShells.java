/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [19/03/2016, 02:41:48 (GMT)]
 */
package vazkii.quark.exploration.feature;

import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.exploration.item.ItemShulkerShell;

public class ShulkerShells extends Feature {

	public static Item shulker_shell;

	public static PotionType levitation;
	public static PotionType long_levitation;

	boolean brewable;

	@Override
	public void setupConfig() {
		brewable = loadPropBool("Brewable for Levitation", "", true);
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		shulker_shell = new ItemShulkerShell();

		if(brewable) {
			levitation = new PotionType(new PotionEffect(MobEffects.levitation, 0, 100));
			long_levitation = new PotionType(new PotionEffect(MobEffects.levitation, 0, 160));
		}
	}

}
