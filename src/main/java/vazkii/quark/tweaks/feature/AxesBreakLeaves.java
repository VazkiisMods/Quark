package vazkii.quark.tweaks.feature;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

public class AxesBreakLeaves extends Feature {

	@SubscribeEvent
	public void calcBreakSpeed(BreakSpeed event) {
		ItemStack stack = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
		if(stack.getItem() instanceof ItemAxe && event.getState().getMaterial() == Material.LEAVES)
			event.setNewSpeed(((ItemTool) stack.getItem()).getDestroySpeed(stack, Blocks.PLANKS.getDefaultState()));
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
