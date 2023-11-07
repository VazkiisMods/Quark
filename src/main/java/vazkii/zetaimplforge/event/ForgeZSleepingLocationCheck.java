package vazkii.zetaimplforge.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import vazkii.zeta.event.ZSleepingLocationCheck;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zetaimplforge.ForgeZeta;

public class ForgeZSleepingLocationCheck implements ZSleepingLocationCheck {
    private final SleepingLocationCheckEvent e;

    public ForgeZSleepingLocationCheck(SleepingLocationCheckEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }


    @Override
    public BlockPos getSleepingLocation() {
        return e.getSleepingLocation();
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
