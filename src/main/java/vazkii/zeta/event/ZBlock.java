package vazkii.zeta.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZBlock extends IZetaPlayEvent {
    LevelAccessor getLevel();
    BlockPos getPos();
    BlockState getState();

    interface Break extends ZBlock {
        Player getPlayer();
    }

    interface EntityPlace extends ZBlock {
        BlockState getPlacedBlock();
    }
}
