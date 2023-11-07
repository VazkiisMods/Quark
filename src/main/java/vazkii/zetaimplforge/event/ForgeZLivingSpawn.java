package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import vazkii.zeta.event.ZLivingSpawn;
import vazkii.zeta.event.bus.ZResult;
import vazkii.zetaimplforge.ForgeZeta;

public class ForgeZLivingSpawn implements ZLivingSpawn {
    private final LivingSpawnEvent e;

    public ForgeZLivingSpawn(LivingSpawnEvent e) {
        this.e = e;
    }

    @Override
    public Mob getEntity() {
        return e.getEntity();
    }

    @Override
    public LevelAccessor getLevel() {
        return e.getLevel();
    }

    @Override
    public double getX() {
        return e.getX();
    }

    @Override
    public double getY() {
        return e.getY();
    }

    @Override
    public double getZ() {
        return e.getZ();
    }

    @Override
    public ZResult getResult() {
        return ForgeZeta.from(e.getResult());
    }

    @Override
    public void setResult(ZResult value) {
        e.setResult(ForgeZeta.to(value));
    }

    public static class CheckSpawn extends ForgeZLivingSpawn implements ZLivingSpawn.CheckSpawn {
        private final LivingSpawnEvent.CheckSpawn e;

        public CheckSpawn(LivingSpawnEvent.CheckSpawn e) {
            super(e);
            this.e = e;
        }

        @Override
        public BaseSpawner getSpawner() {
            return e.getSpawner();
        }

        @Override
        public MobSpawnType getSpawnReason() {
            return e.getSpawnReason();
        }

        public static class Lowest extends ForgeZLivingSpawn.CheckSpawn implements ZLivingSpawn.CheckSpawn.Lowest {
            public Lowest(LivingSpawnEvent.CheckSpawn e) {
                super(e);
            }
        }
    }
}
