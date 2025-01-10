package org.violetmoon.quark.content.tools.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.violetmoon.quark.api.ITrowelable;
import org.violetmoon.quark.api.IUsageTickerOverride;
import org.violetmoon.quark.content.tools.module.TrowelModule;
import org.violetmoon.zeta.item.ZetaItem;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.registry.CreativeTabManager;
import org.violetmoon.zeta.util.ItemNBTHelper;
import org.violetmoon.zeta.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		if (player == null) return InteractionResult.PASS;
		InteractionHand hand = context.getHand();

		List<Integer> targets = new ArrayList<>();
		Inventory inventory = player.getInventory();
		for(int i = 0; i < Inventory.getSelectionSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if(isValidTarget(stack, context))
				targets.add(i);
		}

		if (targets.isEmpty()) return InteractionResult.PASS;

		ItemStack trowel = player.getItemInHand(hand);

		long seed = ItemNBTHelper.getLong(trowel, TAG_PLACING_SEED, 0);
		Random rand = new Random(seed);
		ItemNBTHelper.setLong(trowel, TAG_PLACING_SEED, rand.nextLong());

		int targetSlot = targets.get(rand.nextInt(targets.size()));
		ItemStack toPlaceStack = inventory.getItem(targetSlot);

		player.setItemInHand(hand, toPlaceStack);
		InteractionResult result = toPlaceStack.useOn(new TrowelBlockItemUseContext(context, toPlaceStack));
		//get new item in hand
		ItemStack newHandItem = player.getItemInHand(hand);

		//reset
		player.setItemInHand(hand, trowel);
		inventory.setItem(targetSlot, newHandItem);

		if (result.consumesAction()) {
			CompoundTag cmp = toPlaceStack.serializeNBT();
			ItemNBTHelper.setCompound(trowel, TAG_LAST_STACK, cmp);

			if(TrowelModule.maxDamage > 0)
				MiscUtil.damageStack(player, hand, context.getItemInHand(), 1);
		}

		return result;
	}

	private static boolean isValidTarget(ItemStack stack, UseOnContext context) {
		Item item = stack.getItem();
		//tags have priority and can override these. Dont accidentally tag stuff that has the interface if you want to use it
		if (stack.is(TrowelModule.whitelist)) return true;
		if (stack.is(TrowelModule.blacklist)) return false;
		if (item instanceof ITrowelable t) return t.canBeTroweled(stack, context);
		return !stack.isEmpty() && (item instanceof BlockItem);
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
