package vazkii.zeta.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.LevelAccessor;
import vazkii.zeta.event.bus.IZetaPlayEvent;
import vazkii.zeta.event.bus.Resultable;

public interface ZLivingSpawn extends IZetaPlayEvent, Resultable {
    Mob getEntity();
    LevelAccessor getLevel();
    double getX();
    double getY();
    double getZ();
    interface CheckSpawn extends ZLivingSpawn {
        BaseSpawner getSpawner();
        MobSpawnType getSpawnReason();

        interface Lowest extends CheckSpawn { }
    }
}
