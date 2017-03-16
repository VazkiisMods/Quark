package vazkii.quark.tweaks.feature;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

public class BetterVanillaTextures extends Feature {

	boolean granite, andesite, diorite, bricks, glass, pumpkinFace, pistonModels, bowAnimation;
	
	@Override
	public void setupConfig() {
		granite = loadPropBool("Override Granite", "", true);
		andesite = loadPropBool("Override Andesite", "", true);
		diorite = loadPropBool("Override Diorite", "", true);
		bricks = loadPropBool("Override Bricks", "", true);
		glass = loadPropBool("Override Glass", "", true);
		pumpkinFace = loadPropBool("Override Pumpkin Front Face", "", false);
		pistonModels = loadPropBool("Override Piston Models", "", true);
		bowAnimation = loadPropBool("Override Bow Animation", "", true);
	}
	
	@Override
	public void preInitClient(FMLPreInitializationEvent event) {
		overrideBlock("stone_granite", granite);
		overrideBlock("stone_andesite", andesite);
		overrideBlock("stone_diorite", diorite);
		overrideBlock("brick", bricks);
		overrideBlock("glass", glass);
		overrideBlock("pumpkin_face_off", pumpkinFace);
		
		overrideBlockModel("piston_extended_normal", pistonModels);
		overrideBlockModel("piston_head_normal", pistonModels);
		overrideBlockModel("piston_head_short_sticky", pistonModels);
		overrideBlockModel("piston_head_sticky", pistonModels);
		overrideBlockModel("piston_inventory_sticky", pistonModels);
		overrideBlockModel("sticky_piston", pistonModels);
		
		overrideItemModel("bow", bowAnimation);
	}
	
	private void overrideBlock(String str, boolean flag) {
		if(flag)
			Quark.proxy.addResourceOverride("textures", "blocks", str, "png");
	}
	
	private void overrideBlockModel(String str, boolean flag) {
		if(flag)
			Quark.proxy.addResourceOverride("models", "block", str, "json");
	}
	
	private void overrideItemModel(String str, boolean flag) {
		if(flag)
			Quark.proxy.addResourceOverride("models", "item", str, "json");
	}

	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
