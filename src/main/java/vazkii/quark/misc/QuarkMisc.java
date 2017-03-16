package vazkii.quark.misc;

import vazkii.quark.base.module.Module;
import vazkii.quark.misc.feature.AncientTomes;
import vazkii.quark.misc.feature.AutoJumpHotkey;
import vazkii.quark.misc.feature.ColorRunes;
import vazkii.quark.misc.feature.EndermitesIntoShulkers;
import vazkii.quark.misc.feature.ExtraArrows;
import vazkii.quark.misc.feature.LockDirectionHotkey;
import vazkii.quark.misc.feature.NoteBlockInterface;
import vazkii.quark.misc.feature.NoteBlocksMobSounds;
import vazkii.quark.misc.feature.PanoramaMaker;
import vazkii.quark.misc.feature.SlimeBucket;
import vazkii.quark.misc.feature.SnowGolemPlayerHeads;
import vazkii.quark.misc.feature.SoulPowder;

public class QuarkMisc extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new AncientTomes());
		registerFeature(new AutoJumpHotkey());
		registerFeature(new ColorRunes());
		registerFeature(new EndermitesIntoShulkers());
		registerFeature(new ExtraArrows());
		registerFeature(new NoteBlocksMobSounds(), "Note blocks play mob sounds if there's a head attached");
		registerFeature(new PanoramaMaker());
		registerFeature(new SlimeBucket());
		registerFeature(new SnowGolemPlayerHeads(), "Named snow golems with pumpkins drop player heads if killed by a witch");
		registerFeature(new NoteBlockInterface());
		registerFeature(new SoulPowder());
		registerFeature(new LockDirectionHotkey());
	}
	
}
