/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [24/03/2016, 01:34:38 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import net.minecraft.init.Items;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import vazkii.quark.base.module.Feature;

public class DragonsBreathBottleless extends Feature {

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		Items.dragon_breath.setContainerItem(null);
	}

}
