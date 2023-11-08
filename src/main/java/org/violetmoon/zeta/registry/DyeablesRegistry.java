package org.violetmoon.zeta.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
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
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.recipe.ZetaDyeRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @see org.violetmoon.zeta.client.ClientRegistryExtension
 */
public class DyeablesRegistry {
	protected final Zeta z;

	public final Map<Item, Supplier<Boolean>> dyeableConditions = new HashMap<>();
	public final DyeableLeatherItem SURROGATE = new DyeableLeatherItem() {}; //Simply an accessor for various DyeableLeatherItem default methods

	public DyeablesRegistry(Zeta z) {
		this.z = z;
		z.loadBus.subscribe(this);
	}

	@LoadEvent
	public void register(ZRegister event) {
		ResourceLocation id = event.getRegistry().newResourceLocation("dye_item");
		ZetaDyeRecipe recipe = new ZetaDyeRecipe(id, this);
		event.getRegistry().register(recipe.getSerializer(), id, Registry.RECIPE_SERIALIZER_REGISTRY);
	}

	@LoadEvent
	public void registerPost(ZRegister.Post event) {
		WashingInteraction wosh = new WashingInteraction();
		for(Item item : dyeableConditions.keySet())
			CauldronInteraction.WATER.put(item, wosh);
	}

	class WashingInteraction implements CauldronInteraction {
		//Copy of CauldronInteraction.DYED_ITEM
		@Override
		public InteractionResult interact(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
			if(!isDyed(stack))
				return InteractionResult.PASS;

			if(!level.isClientSide) {
				SURROGATE.clearColor(stack);
				//player.awardStat(Stats.CLEAN_ARMOR);
				LayeredCauldronBlock.lowerFillLevel(state, level, pos);
			}

			return InteractionResult.sidedSuccess(level.isClientSide);
		}
	}

	public void register(Item item) {
		register(item, () -> true);
	}

	public void register(Item item, ZetaModule module) {
		register(item, () -> module.enabled);
	}

	public void register(Item item, Supplier<Boolean> cond) {
		dyeableConditions.put(item, cond);
	}

	public boolean isDyeable(ItemStack stack) {
		Item item = stack.getItem();
		return dyeableConditions.containsKey(item) && dyeableConditions.get(item).get();
	}

	public boolean isDyed(ItemStack stack) {
		return isDyeable(stack) && SURROGATE.hasCustomColor(stack);
	}

	public int getDye(ItemStack stack) {
		return SURROGATE.getColor(stack);
	}

	public void applyDye(ItemStack stack, int color) {
		if(isDyeable(stack))
			SURROGATE.setColor(stack, color);
	}

	public int getColor(ItemStack stack) {
		return isDyed(stack) ? SURROGATE.getColor(stack) : 0xFF_FF_FF;
	}

	// Copy of DyeableLeatherItem
	public ItemStack dyeItem(ItemStack stack, List<DyeItem> dyes) {
		ItemStack itemstack;
		int[] aint = new int[3];
		int i = 0;
		int j = 0;

		if(isDyeable(stack)) {
			itemstack = stack.copy();
			itemstack.setCount(1);
			if(SURROGATE.hasCustomColor(stack)) {
				int k = SURROGATE.getColor(itemstack);
				float f = (float) (k >> 16 & 255) / 255.0F;
				float f1 = (float) (k >> 8 & 255) / 255.0F;
				float f2 = (float) (k & 255) / 255.0F;
				i += (int) (Math.max(f, Math.max(f1, f2)) * 255.0F);
				aint[0] += (int) (f * 255.0F);
				aint[1] += (int) (f1 * 255.0F);
				aint[2] += (int) (f2 * 255.0F);
				++j;
			}

			for(DyeItem dyeitem : dyes) {
				float[] afloat = dyeitem.getDyeColor().getTextureDiffuseColors();
				int i2 = (int) (afloat[0] * 255.0F);
				int l = (int) (afloat[1] * 255.0F);
				int i1 = (int) (afloat[2] * 255.0F);
				i += Math.max(i2, Math.max(l, i1));
				aint[0] += i2;
				aint[1] += l;
				aint[2] += i1;
				++j;
			}

			int j1 = aint[0] / j;
			int k1 = aint[1] / j;
			int l1 = aint[2] / j;
			float f3 = (float) i / (float) j;
			float f4 = (float) Math.max(j1, Math.max(k1, l1));
			j1 = (int) ((float) j1 * f3 / f4);
			k1 = (int) ((float) k1 * f3 / f4);
			l1 = (int) ((float) l1 * f3 / f4);
			int j2 = (j1 << 8) + k1;
			j2 = (j2 << 8) + l1;
			SURROGATE.setColor(itemstack, j2);

			return itemstack;
		}

		return ItemStack.EMPTY;
	}

}
