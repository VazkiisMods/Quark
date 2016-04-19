/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [21/03/2016, 01:09:33 (GMT)]
 */
package vazkii.quark.vanity.feature;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import vazkii.quark.base.module.Feature;
import vazkii.quark.vanity.recipe.FireworkCloningRecipe;

public class FireworkCloning extends Feature {

	@Override
	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(new FireworkCloningRecipe());
		RecipeSorter.register("quark:fireworkCloning", FireworkCloningRecipe.class, RecipeSorter.Category.SHAPELESS, "");
	}

}
