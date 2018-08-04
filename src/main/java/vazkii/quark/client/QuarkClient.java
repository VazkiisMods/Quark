package vazkii.quark.client;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.client.feature.AngryCreepers;
import vazkii.quark.client.feature.AutoJumpHotkey;
import vazkii.quark.client.feature.BetterVanillaTextures;
import vazkii.quark.client.feature.ChestSearchBar;
import vazkii.quark.client.feature.FoodTooltip;
import vazkii.quark.client.feature.GreenerGrass;
import vazkii.quark.client.feature.ImprovedMountHUD;
import vazkii.quark.client.feature.ImprovedSignEdit;
import vazkii.quark.client.feature.ItemsFlashBeforeExpiring;
import vazkii.quark.client.feature.LessIntrusiveShields;
import vazkii.quark.client.feature.MapTooltip;
import vazkii.quark.client.feature.NoPotionShift;
import vazkii.quark.client.feature.PanoramaMaker;
import vazkii.quark.client.feature.RandomAnimalTextures;
import vazkii.quark.client.feature.ShulkerBoxTooltip;
import vazkii.quark.client.feature.UsageTicker;
import vazkii.quark.client.feature.VisualStatDisplay;

public class QuarkClient extends Module {

	@Override
	public void addFeatures() {
		registerFeature(new ChestSearchBar());
		registerFeature(new AngryCreepers(), "Creepers turn red when they're exploding");
		registerFeature(new ShulkerBoxTooltip());
		registerFeature(new FoodTooltip());
		registerFeature(new GreenerGrass());
		registerFeature(new ImprovedMountHUD());
		registerFeature(new MapTooltip());
		registerFeature(new NoPotionShift());
		registerFeature(new RandomAnimalTextures());
		registerFeature(new LessIntrusiveShields());
		registerFeature(new BetterVanillaTextures());
		registerFeature(new VisualStatDisplay());
		registerFeature(new AutoJumpHotkey());
		registerFeature(new PanoramaMaker());
		registerFeature(new ImprovedSignEdit());
		registerFeature(new UsageTicker());
		registerFeature(new ItemsFlashBeforeExpiring());
	}
	
	@Override
	public ItemStack getIconStack() {
		return new ItemStack(Items.ENDER_EYE);
	}
	
}
