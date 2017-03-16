/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [18/03/2016, 22:35:41 (GMT)]
 */
package vazkii.quark.decoration;

import vazkii.quark.base.module.Module;
import vazkii.quark.decoration.feature.BlazeLantern;
import vazkii.quark.decoration.feature.CharcoalBlock;
import vazkii.quark.decoration.feature.ColoredBeds;
import vazkii.quark.decoration.feature.ColoredItemFrames;
import vazkii.quark.decoration.feature.FlatItemFrames;
import vazkii.quark.decoration.feature.IronLadders;
import vazkii.quark.decoration.feature.LeafCarpets;
import vazkii.quark.decoration.feature.LitLamp;
import vazkii.quark.decoration.feature.MoreBanners;
import vazkii.quark.decoration.feature.NetherBrickFenceGate;
import vazkii.quark.decoration.feature.PaperWall;
import vazkii.quark.decoration.feature.VariedBookshelves;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.feature.VariedTrapdoors;

public class QuarkDecoration extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new LitLamp());
		registerFeature(new BlazeLantern());
		registerFeature(new PaperWall());
		registerFeature(new VariedTrapdoors());
		registerFeature(new MoreBanners());
		registerFeature(new NetherBrickFenceGate());
		registerFeature(new ColoredItemFrames());
		registerFeature(new CharcoalBlock());
		registerFeature(new VariedChests());
		registerFeature(new LeafCarpets());
		registerFeature(new ColoredBeds());
		registerFeature(new VariedBookshelves());
		registerFeature(new IronLadders());
		registerFeature(new FlatItemFrames());
	}

}
