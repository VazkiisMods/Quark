package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.eventbus.api.Event;
import vazkii.zeta.event.ZEntityMobGriefing;

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
    public void setResult(Event.Result value) {
        e.setResult(value);
    }
}
