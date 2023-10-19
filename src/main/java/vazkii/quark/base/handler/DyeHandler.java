package vazkii.quark.base.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.recipe.DyeRecipe;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZAddItemColorHandlers;
import vazkii.zeta.event.client.ZClientSetup;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class DyeHandler {

	private static final Map<Item, Supplier<Boolean>> dyeableConditions = new HashMap<>();

	private static final DyeableLeatherItem SURROGATE = new DyeableLeatherItem() {};

	@LoadEvent
	public static void register(ZRegister event) {
		ForgeRegistries.RECIPE_SERIALIZERS.register(Quark.MOD_ID + ":dye_item", DyeRecipe.SERIALIZER);
	}

	@Nonnull
	private static InteractionResult cauldronInteract(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
	      if(!isDyed(stack))
	         return InteractionResult.PASS;

         if(!level.isClientSide) {
            SURROGATE.clearColor(stack);
//            p_175632_.awardStat(Stats.CLEAN_ARMOR);
            LayeredCauldronBlock.lowerFillLevel(state, level, pos);
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
	}

	public static void addAlwaysDyeable(Item item) {
		addDyeable(item, () -> true);
	}

	public static void addDyeable(Item item, QuarkModule module) {
		addDyeable(item, () -> module.enabled);
	}

	public static void addDyeable(Item item, Supplier<Boolean> cond) {
		dyeableConditions.put(item, cond);
	}

	public static boolean isDyeable(ItemStack stack) {
		Item item = stack.getItem();
		return dyeableConditions.containsKey(item) && dyeableConditions.get(item).get();
	}

	public static boolean isDyed(ItemStack stack) {
		return isDyeable(stack) && SURROGATE.hasCustomColor(stack);
	}

	public static int getDye(ItemStack stack) {
		return SURROGATE.getColor(stack);
	}

	public static void applyDye(ItemStack stack, int color) {
		if(isDyeable(stack))
			SURROGATE.setColor(stack, color);
	}

	// Copy of DyeableLeatherItem but for our system
	public static ItemStack dyeItem(ItemStack stack, List<DyeItem> dyes) {
	      ItemStack itemstack = ItemStack.EMPTY;
	      int[] aint = new int[3];
	      int i = 0;
	      int j = 0;

	      if(isDyeable(stack)) {
	         itemstack = stack.copy();
	         itemstack.setCount(1);
	         if (SURROGATE.hasCustomColor(stack)) {
	            int k = SURROGATE.getColor(itemstack);
	            float f = (float)(k >> 16 & 255) / 255.0F;
	            float f1 = (float)(k >> 8 & 255) / 255.0F;
	            float f2 = (float)(k & 255) / 255.0F;
	            i += (int)(Math.max(f, Math.max(f1, f2)) * 255.0F);
	            aint[0] += (int)(f * 255.0F);
	            aint[1] += (int)(f1 * 255.0F);
	            aint[2] += (int)(f2 * 255.0F);
	            ++j;
	         }

	         for(DyeItem dyeitem : dyes) {
	            float[] afloat = dyeitem.getDyeColor().getTextureDiffuseColors();
	            int i2 = (int)(afloat[0] * 255.0F);
	            int l = (int)(afloat[1] * 255.0F);
	            int i1 = (int)(afloat[2] * 255.0F);
	            i += Math.max(i2, Math.max(l, i1));
	            aint[0] += i2;
	            aint[1] += l;
	            aint[2] += i1;
	            ++j;
	         }

	         int j1 = aint[0] / j;
	         int k1 = aint[1] / j;
	         int l1 = aint[2] / j;
	         float f3 = (float)i / (float)j;
	         float f4 = (float)Math.max(j1, Math.max(k1, l1));
	         j1 = (int)((float)j1 * f3 / f4);
	         k1 = (int)((float)k1 * f3 / f4);
	         l1 = (int)((float)l1 * f3 / f4);
	         int j2 = (j1 << 8) + k1;
	         j2 = (j2 << 8) + l1;
	         SURROGATE.setColor(itemstack, j2);

	         return itemstack;
	      }

	      return ItemStack.EMPTY;
	   }

	public static class Client {
		@LoadEvent
		public static void colorHandlers(ZAddItemColorHandlers event) {
			ItemPropertyFunction fun = (s, e, l, i) -> DyeHandler.isDyed(s) ? 1 : 0;
			ItemColor color = (s, l) -> {
				if(l != 0 || !isDyed(s))
					return 0xFFFFFF;

				return SURROGATE.getColor(s);
			};

			ResourceLocation res = new ResourceLocation("quark_dyed");
			for(Item item : dyeableConditions.keySet()) {
				ItemProperties.register(item, res, fun);

				event.register(color, item);

				CauldronInteraction.WATER.put(item, DyeHandler::cauldronInteract);
			}
		}
	}
}
