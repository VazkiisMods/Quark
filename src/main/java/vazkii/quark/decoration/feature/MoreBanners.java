/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [25/03/2016, 19:47:46 (GMT)]
 */
package vazkii.quark.decoration.feature;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import vazkii.quark.base.module.Feature;

public class MoreBanners extends Feature {

	boolean dragon, eye, shield, sword;

	@Override
	public void setupConfig() {
		dragon = loadPropBool("Dragon", "", true);
		eye = loadPropBool("Eye", "", true);
		shield = loadPropBool("Shield", "", true);
		sword = loadPropBool("Sword", "", true);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		addPattern(dragon, "dragon", "dr", new ItemStack(Items.skull, 1, 5));
		addPattern(eye, "eye", "ey", new ItemStack(Items.ender_eye));
		addPattern(shield, "shield", "sh", new ItemStack(Items.iron_chestplate));
		addPattern(sword, "sword", "sw", new ItemStack(Items.iron_sword));
	}

	public static void addPattern(boolean doit, String name, String id, ItemStack craftingItem) {
		if(!doit)
			return;

		name = "quark_" + name;
		id = "q_" + id;
		EnumHelper.addEnum(TileEntityBanner.EnumBannerPattern.class, name.toUpperCase(), new Class[] { String.class, String.class, ItemStack.class }, new Object[] { name, id, craftingItem });
	}

}
