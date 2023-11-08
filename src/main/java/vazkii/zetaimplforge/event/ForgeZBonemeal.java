package vazkii.zetaimplforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.BonemealEvent;
import vazkii.zeta.event.ZBonemeal;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zetaimplforge.ForgeZeta;

public class ForgeZBonemeal implements ZBonemeal {
    private final BonemealEvent e;

    public ForgeZBonemeal(BonemealEvent e) {
        this.e = e;
    }

    @Override
    public Level getLevel() {
        return e.getLevel();
    }

    @Override
    public BlockPos getPos() {
        return e.getPos();
    }

    @Override
    public BlockState getBlock() {
        return e.getBlock();
    }

    @Override
    public ItemStack getStack() {
        return e.getStack();
    }

    @Override
    public ZResult getResult() {
        return ForgeZeta.from(e.getResult());
    }

    @Override
    public void setResult(ZResult value) {
        e.setResult(ForgeZeta.to(value));
    }
}
