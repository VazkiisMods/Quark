package org.violetmoon.quark.content.tools.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.violetmoon.quark.base.item.QuarkItem;
import org.violetmoon.quark.content.tools.entity.ParrotEgg;
import org.violetmoon.quark.content.tools.module.ParrotEggsModule;
import org.violetmoon.zeta.module.ZetaModule;

import javax.annotation.Nonnull;

public class ParrotEggItem extends QuarkItem {
	private final int variant;

	public ParrotEggItem(String suffix, int variant, ZetaModule module) {
		super("egg_parrot_" + suffix, module,
				new Item.Properties()
						.stacksTo(16)
						.tab(CreativeModeTab.TAB_MATERIALS));
		this.variant = variant;
	}

	@Nonnull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
		if (!world.isClientSide) {
			ParrotEgg parrotEgg = new ParrotEgg(world, player);
			parrotEgg.setItem(stack);
			parrotEgg.setVariant(variant);
			parrotEgg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
			world.addFreshEntity(parrotEgg);
			
			if(player instanceof ServerPlayer sp)
				ParrotEggsModule.throwParrotEggTrigger.trigger(sp);
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		if (!player.getAbilities().instabuild)
			stack.shrink(1);

		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}
}
