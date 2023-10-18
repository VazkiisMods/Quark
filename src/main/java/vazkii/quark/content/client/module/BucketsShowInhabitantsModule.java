package vazkii.quark.content.client.module;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZAddItemColorHandlers;
import vazkii.zeta.event.client.ZClientSetup;
import vazkii.zeta.util.ItemNBTHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.mobs.entity.Crab;
import vazkii.quark.content.mobs.module.CrabsModule;
import vazkii.quark.content.tools.item.SlimeInABucketItem;
import vazkii.quark.content.tools.module.SlimeInABucketModule;
import vazkii.quark.mixin.client.accessor.AccessorItemColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.IntUnaryOperator;

@LoadModule(category = "client")
public class BucketsShowInhabitantsModule extends QuarkModule {

	@Config
	public static boolean showAxolotls = true;
	@Config
	public static boolean showCrabs = true;
	@Config
	public static boolean showTropicalFish = true;
	@Config
	public static boolean showShinySlime = true;

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void clientSetup(ZClientSetup e) {
		e.enqueueWork(() -> {
			ItemProperties.register(Items.AXOLOTL_BUCKET, new ResourceLocation(Quark.MOD_ID, "variant"),
				new MobBucketVariantProperty(Axolotl.Variant.BY_ID.length, () -> showAxolotls));
			ItemProperties.register(CrabsModule.crab_bucket, new ResourceLocation(Quark.MOD_ID, "variant"),
				new MobBucketVariantProperty(Crab.COLORS, () -> showCrabs));

			ItemProperties.register(SlimeInABucketModule.slime_in_a_bucket, new ResourceLocation(Quark.MOD_ID, "shiny"),
				new ShinyMobBucketProperty(() -> showShinySlime && VariantAnimalTexturesModule.enabled() && VariantAnimalTexturesModule.enableShinySlime));

			ItemProperties.register(Items.TROPICAL_FISH_BUCKET, new ResourceLocation(Quark.MOD_ID, "base"),
				new TropicalFishBucketVariantProperty(TropicalFish::getBaseVariant, () -> showTropicalFish));
			ItemProperties.register(Items.TROPICAL_FISH_BUCKET, new ResourceLocation(Quark.MOD_ID, "pattern"),
				new TropicalFishBucketVariantProperty(TropicalFish::getPatternVariant, () -> showTropicalFish));
		});
	}

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void registerItemColors(ZAddItemColorHandlers evt) {
		Holder.Reference<Item> tropicalBucket = ForgeRegistries.ITEMS.getDelegateOrThrow(Items.TROPICAL_FISH_BUCKET);
		ItemColor parent = ((AccessorItemColors) evt.getItemColors()).quark$getItemColors().get(tropicalBucket);
		evt.register(new TropicalFishBucketColor(parent, () -> showTropicalFish), Items.TROPICAL_FISH_BUCKET);
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	private class MobBucketVariantProperty implements ItemPropertyFunction {

		private final int maxVariants;
		private final BooleanSupplier featureEnabled;

		public MobBucketVariantProperty(int maxVariants, BooleanSupplier featureEnabled) {
			this.maxVariants = maxVariants;
			this.featureEnabled = featureEnabled;
		}

		@Override
		public float call(@Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int id) {
			if (!enabled || !featureEnabled.getAsBoolean())
				return 0;

			return ItemNBTHelper.getInt(stack, Axolotl.VARIANT_TAG, 0) % maxVariants;
		}
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	private class ShinyMobBucketProperty implements ItemPropertyFunction {

		private final BooleanSupplier featureEnabled;

		public ShinyMobBucketProperty(BooleanSupplier featureEnabled) {
			this.featureEnabled = featureEnabled;
		}

		@Override
		public float call(@Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int id) {
			if (!enabled || !featureEnabled.getAsBoolean())
				return 0;

			CompoundTag data = ItemNBTHelper.getCompound(stack, SlimeInABucketItem.TAG_ENTITY_DATA, true);
			if (data != null && data.hasUUID("UUID")) {
				UUID uuid = data.getUUID("UUID");
				if (VariantAnimalTexturesModule.isShiny(uuid))
					return 1;
			}

			return 0;
		}
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	private class TropicalFishBucketVariantProperty implements ItemPropertyFunction {

		private final IntUnaryOperator extractor;
		private final BooleanSupplier featureEnabled;

		public TropicalFishBucketVariantProperty(IntUnaryOperator extractor, BooleanSupplier featureEnabled) {
			this.extractor = extractor;
			this.featureEnabled = featureEnabled;
		}

		@Override
		public float call(@Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int id) {
			if (!enabled || !featureEnabled.getAsBoolean())
				return 0;

			CompoundTag tag = stack.getTag();
			if (tag != null && tag.contains(TropicalFish.BUCKET_VARIANT_TAG, Tag.TAG_INT)) {
				int variant = tag.getInt(TropicalFish.BUCKET_VARIANT_TAG);
				return extractor.applyAsInt(variant) + 1;
			}

			return 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private class TropicalFishBucketColor implements ItemColor {

		@Nullable
		private final ItemColor parent;
		private final BooleanSupplier featureEnabled;

		public TropicalFishBucketColor(@Nullable ItemColor parent, BooleanSupplier featureEnabled) {
			this.parent = parent;
			this.featureEnabled = featureEnabled;
		}

		@Override
		public int getColor(@Nonnull ItemStack stack, int layer) {
			if (enabled && featureEnabled.getAsBoolean() && (layer == 1 || layer == 2)) {
				CompoundTag tag = stack.getTag();
				if (tag != null && tag.contains(TropicalFish.BUCKET_VARIANT_TAG, Tag.TAG_INT)) {
					int variant = tag.getInt(TropicalFish.BUCKET_VARIANT_TAG);

					DyeColor dyeColor = layer == 1 ? TropicalFish.getBaseColor(variant) : TropicalFish.getPatternColor(variant);
					float[] colorComponents = dyeColor.getTextureDiffuseColors();

					return ((int) (colorComponents[0] * 255) << 16) |
						((int) (colorComponents[1] * 255) << 8) |
						(int) (colorComponents[2] * 255);
				}
			}

			return parent != null ? parent.getColor(stack, layer) : -1;
		}
	}
}
