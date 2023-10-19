package vazkii.quark.content.client.module;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

@LoadModule(category = "client", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class VariantAnimalTexturesModule extends QuarkModule {

	private static ListMultimap<VariantTextureType, ResourceLocation> textures;
	private static Map<VariantTextureType, ResourceLocation> shinyTextures;

	private static final int COW_COUNT = 4;
	private static final int PIG_COUNT = 3;
	private static final int CHICKEN_COUNT = 6;

	@Config public static boolean enableCow = true;
	@Config public static boolean enablePig = true;
	@Config public static boolean enableChicken = true;
	@Config public static boolean enableShinyRabbit = true;
	@Config public static boolean enableShinyLlama = true;
	@Config public static boolean enableShinyDolphin = true;
	@Config public static boolean enableShinySlime = true;
	@Config public static boolean enableLGBTBees = true;

	@Config public static boolean everyBeeIsLGBT = false;

	@Config(description = "The chance for an animal to have a special \"Shiny\" skin, like a shiny pokemon. This is 1 in X. Set to 0 to disable.")
	public static int shinyAnimalChance = 2048;

	@Config(description = "If a shiny animal should emit occasional sparkles.")
	public static boolean shinySparkles = true;

	private static boolean isEnabled;

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		if(!enabled)
			return;

		textures = Multimaps.newListMultimap(new EnumMap<>(VariantTextureType.class), ArrayList::new);
		shinyTextures = new HashMap<>();

		registerTextures(VariantTextureType.COW, COW_COUNT, new ResourceLocation("textures/entity/cow/cow.png"));
		registerTextures(VariantTextureType.PIG, PIG_COUNT, new ResourceLocation("textures/entity/pig/pig.png"));
		registerTextures(VariantTextureType.CHICKEN, CHICKEN_COUNT, new ResourceLocation("textures/entity/chicken.png"));
		registerShiny(VariantTextureType.RABBIT);
		registerShiny(VariantTextureType.LLAMA);
		registerShiny(VariantTextureType.DOLPHIN);
		registerShiny(VariantTextureType.SLIME);
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		// Pass over to a static reference for easier computing the coremod hook
		isEnabled = this.enabled;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getCowTexture(Cow entity) {
		if (!isEnabled || !enableCow)
			return null;
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.COW);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getPigTexture(Pig entity) {
		if (!isEnabled || !enablePig)
			return null;
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.PIG);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getChickenTexture(Chicken entity) {
		if (!isEnabled || !enableChicken)
			return null;
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.CHICKEN);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getRabbitTexture(Rabbit entity) {
		if (!isEnabled || !enableShinyRabbit)
			return null;
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.RABBIT, () -> null);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getLlamaTexture(Llama entity) {
		if (!isEnabled || !enableShinyLlama)
			return null;
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.LLAMA, () -> null);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getDolphinTexture(Dolphin entity) {
		if (!isEnabled || !enableShinyDolphin)
			return null;
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.DOLPHIN, () -> null);
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getSlimeTexture(Slime entity) {
		if (!isEnabled || !enableShinySlime)
			return null;
		return VariantAnimalTexturesModule.getTextureOrShiny(entity, VariantTextureType.SLIME, () -> null);
	}

	private static final List<String> BEE_VARIANTS = List.of(
			"acebee", "agenbee", "arobee", "beefluid", "beesexual",
			"beequeer", "enbee", "gaybee", "interbee", "lesbeean",
			"panbee", "polysexbee", "transbee", "helen");

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getBeeTexture(Bee entity) {
		if (!isEnabled || !enableLGBTBees)
			return null;

		UUID id = entity.getUUID();
		long most = id.getMostSignificantBits();

		// From https://news.gallup.com/poll/329708/lgbt-identification-rises-latest-estimate.aspx
		final double lgbtChance = 0.056;
		boolean lgbt = VariantAnimalTexturesModule.everyBeeIsLGBT || (new Random(most)).nextDouble() < lgbtChance;

		if(entity.hasCustomName() || lgbt) {
			String custName = "";
			if (entity.hasCustomName()) {
				Component name = entity.getCustomName();
				if (name != null)
					custName = name.getString();
			}

			String name = custName.toLowerCase(Locale.ROOT);

			if(!BEE_VARIANTS.contains(name)) {
				if(custName.matches("wire(se|bee)gal"))
					name = "enbee";
				else if(lgbt)
					name = BEE_VARIANTS.get(Math.abs((int) (most % (BEE_VARIANTS.size() - 1)))); // -1 to not spawn helen bee naturally
			}

			if(BEE_VARIANTS.contains(name)) {
				String type = "normal";
				boolean angery = entity.hasStung();
				boolean nectar = entity.hasNectar();

				if(angery)
					type = nectar ? "angry_nectar" : "angry";
				else if(nectar)
					type = "nectar";

				String path = String.format("textures/model/entity/variants/bees/%s/%s.png", name, type);
				return new ResourceLocation(Quark.MOD_ID, path);
			}
		}

		return null;
	}

	public static boolean enabled() {
		return isEnabled;
	}

	public static boolean isShiny(UUID id) {
		long most = id.getMostSignificantBits();
		return shinyAnimalChance > 0 && most % shinyAnimalChance == 0;
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isSparkly(Entity e) {
		EntityType<?> type = e.getType();
		if ((type != EntityType.COW || !enableCow) &&
				(type != EntityType.PIG || !enablePig) &&
				(type != EntityType.CHICKEN || !enableChicken) &&
				(type != EntityType.RABBIT || !enableShinyRabbit) &&
				(type != EntityType.LLAMA || !enableShinyLlama) &&
				(type != EntityType.DOLPHIN || !enableShinyDolphin))
			return false;

		return isShiny(e.getUUID());
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void doShinySparkles(LivingEvent.LivingTickEvent event) {
		if (!shinySparkles)
			return;
		LivingEntity entity = event.getEntity();
		Level level = entity.getLevel();
		if (level.isClientSide() && level.getGameTime() % 10 == 0) {
			if (isSparkly(entity)) {
				double angle = Math.random() * 2 * Math.PI;
				double dist = Math.random() * 0.5 + 0.25;
				double dX = Math.cos(angle) * dist;
				double dY = entity.getDimensions(entity.getPose()).height + (Math.random() - 0.5) * 0.2;
				double dZ = Math.sin(angle) * dist;
				level.addParticle(ParticleTypes.HAPPY_VILLAGER, entity.getX() + dX, entity.getY() + dY, entity.getZ() + dZ, Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getTextureOrShiny(Entity e, VariantTextureType type) {
		return getTextureOrShiny(e, type, () -> getRandomTexture(e, type));
	}

	@OnlyIn(Dist.CLIENT)
	public static ResourceLocation getTextureOrShiny(Entity e, VariantTextureType type, Supplier<ResourceLocation> nonShiny) {
		if(isShiny(e.getUUID()))
			return shinyTextures.get(type);

		return nonShiny.get();
	}

	@OnlyIn(Dist.CLIENT)
	private static ResourceLocation getRandomTexture(Entity e, VariantTextureType type) {
		List<ResourceLocation> styles = textures.get(type);

		UUID id = e.getUUID();
		long most = id.getMostSignificantBits();
		int choice = Math.abs((int) (most % styles.size()));
		return styles.get(choice);
	}

	@OnlyIn(Dist.CLIENT)
	private static void registerTextures(VariantTextureType type, int count, ResourceLocation vanilla) {
		String name = type.name().toLowerCase(Locale.ROOT);
		for(int i = 1; i < count + 1; i++)
			textures.put(type, new ResourceLocation(Quark.MOD_ID, String.format("textures/model/entity/variants/%s%d.png", name, i)));

		if(vanilla != null)
			textures.put(type, vanilla);
		registerShiny(type);
	}

	private static void registerShiny(VariantTextureType type) {
		shinyTextures.put(type, new ResourceLocation(Quark.MOD_ID, String.format("textures/model/entity/variants/%s_shiny.png", type.name().toLowerCase(Locale.ROOT))));
	}

	public enum VariantTextureType {
		COW, PIG, CHICKEN, LLAMA, RABBIT, DOLPHIN, SLIME
	}

}
