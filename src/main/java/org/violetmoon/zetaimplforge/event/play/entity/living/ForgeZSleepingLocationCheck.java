package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import org.violetmoon.zeta.event.bus.ZResult;
import org.violetmoon.zeta.event.play.entity.living.ZSleepingLocationCheck;
import org.violetmoon.zetaimplforge.ForgeZeta;

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
