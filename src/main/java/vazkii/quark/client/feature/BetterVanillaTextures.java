package vazkii.quark.client.feature;

import java.util.function.BiConsumer;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

public class BetterVanillaTextures extends Feature {

	boolean granite, andesite, diorite, bricks, glass, pumpkinFace, pistonModels, bowAnimation, observer;
	
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
		observer = loadPropBool("Override Observer", "", true);
	}
	
	@Override
	public void preInitClient(FMLPreInitializationEvent event) {
		overrideBlock("stone_granite", granite);
		overrideBlock("stone_andesite", andesite);
		overrideBlock("stone_diorite", diorite);
		overrideBlock("brick", bricks);
		overrideBlock("glass", glass);
		overrideBlock("pumpkin_face_off", pumpkinFace);
		
		batch(this::overrideBlockModel, pistonModels,
				"piston_extended_normal", "piston_head_normal", "piston_head_short_sticky",
				"piston_head_sticky", "piston_inventory_sticky", "sticky_piston");
		
		batch(this::overrideBlockModel, observer,
				"observer", "observer_powered");
		
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
	
	private void batch(BiConsumer<String, Boolean> f, boolean flag, String... vars) {
		for(String s : vars)
			f.accept(s, flag);
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
}
