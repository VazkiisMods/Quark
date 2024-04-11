package org.violetmoon.quark.content.tools.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.violetmoon.quark.api.ITrowelable;
import org.violetmoon.quark.api.IUsageTickerOverride;
import org.violetmoon.quark.content.tools.module.TrowelModule;
import org.violetmoon.zeta.item.ZetaItem;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.ItemNBTHelper;
import org.violetmoon.zeta.util.MiscUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;

public class TrowelItem extends ZetaItem implements IUsageTickerOverride {

	private static final String TAG_PLACING_SEED = "placing_seed";
	private static final String TAG_LAST_STACK = "last_stack";

	public TrowelItem(ZetaModule module) {
		super("trowel", module, new Item.Properties()
				.durability(255));
		CreativeTabManager.addToCreativeTabNextTo(CreativeModeTabs.TOOLS_AND_UTILITIES, this, Items.SHEARS, false);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		InteractionHand hand = context.getHand();

		List<ItemStack> targets = new ArrayList<>();
		for(int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if(isValidTarget(stack))
				targets.add(stack);
		}

		ItemStack ourStack = player.getItemInHand(hand);
		if(targets.isEmpty())
			return InteractionResult.PASS;

		long seed = ItemNBTHelper.getLong(ourStack, TAG_PLACING_SEED, 0);
		Random rand = new Random(seed);
		ItemNBTHelper.setLong(ourStack, TAG_PLACING_SEED, rand.nextLong());

		ItemStack target = targets.get(rand.nextInt(targets.size()));
		int count = target.getCount();
		InteractionResult result = placeBlock(target, context);
		if(player.getAbilities().instabuild)
			target.setCount(count);

		if(result.consumesAction()) {
			CompoundTag cmp = target.serializeNBT();
			ItemNBTHelper.setCompound(ourStack, TAG_LAST_STACK, cmp);

			if(TrowelModule.maxDamage > 0)
				MiscUtil.damageStack(player, hand, context.getItemInHand(), 1);
		}

		return result;
	}

	private InteractionResult placeBlock(ItemStack itemstack, UseOnContext context) {
		if(isValidTarget(itemstack)) {
			Item item = itemstack.getItem();

			Player player = context.getPlayer();
			ItemStack restore = itemstack;
			if(player != null) {
				restore = player.getItemInHand(context.getHand());
				player.setItemInHand(context.getHand(), itemstack);
			}
			InteractionResult res = item.useOn(new TrowelBlockItemUseContext(context, itemstack));
			if(player != null) {
				player.setItemInHand(context.getHand(), restore);
			}
			return res;
		}

		return InteractionResult.PASS;
	}

	private static boolean isValidTarget(ItemStack stack) {
		Item item = stack.getItem();
		return !stack.isEmpty() && (item instanceof BlockItem || item instanceof ITrowelable);
	}

	public static ItemStack getLastStack(ItemStack stack) {
		CompoundTag cmp = ItemNBTHelper.getCompound(stack, TAG_LAST_STACK, false);
		return ItemStack.of(cmp);
	}

	@Override
	public int getMaxDamageZeta(ItemStack stack) {
		return TrowelModule.maxDamage;
	}

	@Override
	public boolean canBeDepleted() {
		return TrowelModule.maxDamage > 0;
	}

	@Override
	public ItemStack getUsageTickerItem(ItemStack stack) {
		return getLastStack(stack);
	}

	class TrowelBlockItemUseContext extends BlockPlaceContext {

		public TrowelBlockItemUseContext(UseOnContext context, ItemStack stack) {
			super(context.getLevel(), context.getPlayer(), context.getHand(), stack,
					new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()));
		}

	}

}
