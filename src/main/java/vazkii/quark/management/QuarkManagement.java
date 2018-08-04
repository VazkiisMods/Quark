/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/04/2016, 16:31:28 (GMT)]
 */
package vazkii.quark.management;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.management.feature.AutomaticToolRestock;
import vazkii.quark.management.feature.BetterCraftShifting;
import vazkii.quark.management.feature.ChangeHotbarKeybind;
import vazkii.quark.management.feature.ChestButtons;
import vazkii.quark.management.feature.ChestsInBoats;
import vazkii.quark.management.feature.DeleteItems;
import vazkii.quark.management.feature.FToSwitchItems;
import vazkii.quark.management.feature.FavoriteItems;
import vazkii.quark.management.feature.InventorySorting;
import vazkii.quark.management.feature.LinkItems;
import vazkii.quark.management.feature.RightClickAddToShulkerBox;
import vazkii.quark.management.feature.StoreToChests;

public class QuarkManagement extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new StoreToChests());
		registerFeature(new FToSwitchItems(), "Press F in the inventory to switch item to main hand");
		registerFeature(new ChestButtons());
		registerFeature(new FavoriteItems(), "Ctrl-click an item to favorite it. Favorited items aren't stored by the other features here");
		registerFeature(new LinkItems(), "Press T in the inventory to link items to chat");
		registerFeature(new DeleteItems(), "Press Ctrl-DELETE in the inventory to delete an item");
		registerFeature(new InventorySorting());
		registerFeature(new BetterCraftShifting());
		registerFeature(new ChangeHotbarKeybind());
		registerFeature(new AutomaticToolRestock());
		registerFeature(new RightClickAddToShulkerBox());
		registerFeature(new ChestsInBoats());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Blocks.CHEST);
	}

}
