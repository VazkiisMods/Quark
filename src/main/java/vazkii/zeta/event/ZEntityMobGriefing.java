package vazkii.zeta.event;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZEntityMobGriefing extends IZetaPlayEvent {
    Entity getEntity();
    void setResult(Event.Result value);
}
