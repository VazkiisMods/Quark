package vazkii.zeta.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.Resultable;

public interface ZBonemeal extends IZetaPlayEvent, Resultable {
    Level getLevel();
    BlockPos getPos();
    BlockState getBlock();
    ItemStack getStack();
}
