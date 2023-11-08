package org.violetmoon.zeta.client.event.play;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

import java.util.List;

public interface ZGatherTooltipComponents extends IZetaPlayEvent {
	ItemStack getItemStack();
	int getScreenWidth();
	int getScreenHeight();
	List<Either<FormattedText, TooltipComponent>> getTooltipElements();
	int getMaxWidth();
	void setMaxWidth(int maxWidth);
}
