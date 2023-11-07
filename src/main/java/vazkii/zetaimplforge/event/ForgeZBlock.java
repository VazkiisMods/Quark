package vazkii.zetaimplforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import vazkii.zeta.event.ZBlock;

public class ForgeZBlock implements ZBlock {
    private final BlockEvent e;

    public ForgeZBlock(BlockEvent e) {
        this.e = e;
    }

    @Override
    public LevelAccessor getLevel() {
        return e.getLevel();
    }

    @Override
    public BlockPos getPos() {
        return e.getPos();
    }

    @Override
    public BlockState getState() {
        return e.getState();
    }

    public static class Break extends ForgeZBlock implements ZBlock.Break {
        private final BlockEvent.BreakEvent e;

        public Break(BlockEvent.BreakEvent e) {
            super(e);
            this.e = e;
        }

        @Override
        public Player getPlayer() {
            return e.getPlayer();
        }
    }

    public static class EntityPlace extends ForgeZBlock implements ZBlock.EntityPlace {
        private final BlockEvent.EntityPlaceEvent e;

        public EntityPlace(BlockEvent.EntityPlaceEvent e) {
            super(e);
            this.e = e;
        }

        @Override
        public BlockState getPlacedBlock() {
            return e.getPlacedBlock();
        }
    }
}
