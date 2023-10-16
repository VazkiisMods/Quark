package vazkii.arl.container.slot;

import java.util.function.Predicate;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotFiltered extends Slot {

	private final Predicate<ItemStack> pred;
	
	public SlotFiltered(Container inventoryIn, int index, int xPosition, int yPosition, Predicate<ItemStack> pred) {
		super(inventoryIn, index, xPosition, yPosition);
		this.pred = pred;
	}
	
	@Override
	public boolean mayPlace(ItemStack stack) {
		return pred.test(stack);
	}
	

}
