/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * 
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 * 
 * File Created @ [02/04/2016, 16:31:28 (GMT)]
 */
package vazkii.quark.management;

import vazkii.quark.base.module.Module;
import vazkii.quark.management.feature.ChestButtons;
import vazkii.quark.management.feature.FToSwitchItems;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.StoreToChests;

public class QuarkManagement extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new StoreToChests());
		registerFeature(new FToSwitchItems(), "Press F in the inventory to switch item to main hand");
		registerFeature(new ChestButtons());
		registerFeature(new FavoriteItems(), "Ctrl-click an item to favorite it. Favorited items aren't stored by the other features here");
	}
	
}
