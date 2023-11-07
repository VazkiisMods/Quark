package vazkii.zeta.item.ext;

import net.minecraft.world.item.ItemStack;

public interface ItemExtensionFactory {

	IZetaItemExtensions getInternal(ItemStack stack);

	default IZetaItemExtensions get(ItemStack stack) {
		if(stack.getItem() instanceof IZetaItemExtensions ext)
			return ext;
		else
			return getInternal(stack);
	}

}
