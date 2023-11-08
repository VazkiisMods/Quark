package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import vazkii.zeta.event.ZEntityMobGriefing;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zetaimplforge.ForgeZeta;

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
