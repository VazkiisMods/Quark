package vazkii.quark.misc;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.misc.feature.AncientTomes;
import vazkii.quark.misc.feature.BlackAsh;
import vazkii.quark.misc.feature.ColorRunes;
import vazkii.quark.misc.feature.EnderdragonScales;
import vazkii.quark.misc.feature.EndermitesIntoShulkers;
import vazkii.quark.misc.feature.ExtraArrows;
import vazkii.quark.misc.feature.ExtraPotions;
import vazkii.quark.misc.feature.LockDirectionHotkey;
import vazkii.quark.misc.feature.MapMarkers;
import vazkii.quark.misc.feature.NoteBlockInterface;
import vazkii.quark.misc.feature.NoteBlocksMobSounds;
import vazkii.quark.misc.feature.PlaceVanillaDusts;
import vazkii.quark.misc.feature.PoisonPotatoUsage;
import vazkii.quark.misc.feature.SlimeBucket;
import vazkii.quark.misc.feature.SnowGolemPlayerHeads;
import vazkii.quark.misc.feature.SoulPowder;
import vazkii.quark.misc.feature.ThrowableDragonBreath;
import vazkii.quark.misc.feature.UtilityRecipes;

public class QuarkMisc extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new AncientTomes());
		registerFeature(new ColorRunes());
		registerFeature(new EndermitesIntoShulkers());
		registerFeature(new ExtraArrows());
		registerFeature(new NoteBlocksMobSounds(), "Note blocks play mob sounds if there's a head attached");
		registerFeature(new SlimeBucket());
		registerFeature(new SnowGolemPlayerHeads(), "Named snow golems with pumpkins drop player heads if killed by a witch");
		registerFeature(new NoteBlockInterface());
		registerFeature(new SoulPowder());
		registerFeature(new LockDirectionHotkey());
		registerFeature(new EnderdragonScales());
		registerFeature(new PoisonPotatoUsage());
		registerFeature(new ThrowableDragonBreath());
		registerFeature(new BlackAsh());
		registerFeature(new PlaceVanillaDusts());
		registerFeature(new MapMarkers());
		registerFeature(new ExtraPotions());
		registerFeature(new UtilityRecipes());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Items.CARROT_ON_A_STICK);
	}
	
}
