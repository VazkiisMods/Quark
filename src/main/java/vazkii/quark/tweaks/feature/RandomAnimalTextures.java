package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.client.render.random.RenderChickenRandom;
import vazkii.quark.tweaks.client.render.random.RenderCowRandom;
import vazkii.quark.tweaks.client.render.random.RenderPigRandom;

public class RandomAnimalTextures extends Feature {

	private static ListMultimap<RandomTextureType, ResourceLocation> textures;
	
	private static final int COW_COUNT = 4;
	private static final int PIG_COUNT = 4;

	public static boolean enableCow, enablePig, enableChicken, enableChick;
	
	@Override
	public void setupConfig() {
		enableCow = loadPropBool("Enable Cow", "", true);
		enablePig = loadPropBool("Enable Pig", "", true);
		enableChicken = loadPropBool("Enable Chicken", "", true);
		enableChick = loadPropBool("Enable Chick", "", true);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void preInitClient(FMLPreInitializationEvent event) {
		textures = Multimaps.newListMultimap(new EnumMap(RandomTextureType.class), () -> new ArrayList());
		
		registerTextures(RandomTextureType.COW, 4, new ResourceLocation("textures/entity/cow/cow.png"));
		registerTextures(RandomTextureType.PIG, 4, new ResourceLocation("textures/entity/pig/pig.png"));
		registerTextures(RandomTextureType.CHICKEN, 4, new ResourceLocation("textures/entity/chicken.png"));
		registerTextures(RandomTextureType.CHICK, 3, null);

		registerOverride(EntityCow.class, RenderCowRandom.factory(), enableCow);
		registerOverride(EntityPig.class, RenderPigRandom.factory(), enablePig);
		registerOverride(EntityChicken.class, RenderChickenRandom.factory(), enableChicken);
	}

	@SideOnly(Side.CLIENT)
	public static ResourceLocation getRandomTexture(Entity e, RandomTextureType type) {
		List<ResourceLocation> styles = textures.get(type);
		UUID id = e.getUniqueID();
		int choice = Math.abs((int) (id.getMostSignificantBits() % styles.size()));
		return styles.get(choice);
	}
	
	private static void registerTextures(RandomTextureType type, int count, ResourceLocation vanilla) {
		String name = type.name().toLowerCase();
		for(int i = 1; i < count + 1; i++)
			textures.put(type, new ResourceLocation(LibMisc.MOD_ID, String.format("textures/entity/random/%s%d.png", name, i)));
		
		if(vanilla != null)
			textures.put(type, vanilla);
	}
	
	private static <T extends Entity>void registerOverride(Class<T> clazz, IRenderFactory<? super T> factory, boolean enabled) {
		if(enabled)
			RenderingRegistry.registerEntityRenderingHandler(clazz, factory);
	}
	
	public static enum RandomTextureType {
		COW, PIG, CHICKEN, CHICK
	}
	
}

