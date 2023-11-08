package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import vazkii.zeta.event.ZLivingFall;

public class ForgeZLivingFall implements ZLivingFall {
    private final LivingFallEvent e;

    public ForgeZLivingFall(LivingFallEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }

    @Override
    public float getDistance() {
        return e.getDistance();
    }

    @Override
    public void setDistance(float distance) {
        e.setDistance(distance);
    }
}
