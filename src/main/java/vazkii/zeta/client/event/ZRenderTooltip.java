package vazkii.zeta.client.event;

import com.mojang.datafixers.util.Either;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import vazkii.zeta.event.bus.IZetaPlayEvent;

import java.util.List;

public interface ZRenderTooltip extends IZetaPlayEvent {
    interface GatherComponents extends IZetaPlayEvent, ZRenderTooltip {
        ItemStack getItemStack();
        List<Either<FormattedText, TooltipComponent>> getTooltipElements();

        interface Low extends GatherComponents { }
    }
}
