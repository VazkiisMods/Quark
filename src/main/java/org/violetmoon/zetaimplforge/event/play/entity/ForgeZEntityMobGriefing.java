package org.violetmoon.zetaimplforge.event.play.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import org.violetmoon.zeta.event.bus.ZResult;
import org.violetmoon.zeta.event.play.entity.ZEntityMobGriefing;
import org.violetmoon.zetaimplforge.ForgeZeta;

public class ForgeZEntityMobGriefing implements ZEntityMobGriefing {
    private final EntityMobGriefingEvent e;

    public ForgeZEntityMobGriefing(EntityMobGriefingEvent e) {
        this.e = e;
    }

    @Override
    public Entity getEntity() {
        return e.getEntity();
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
