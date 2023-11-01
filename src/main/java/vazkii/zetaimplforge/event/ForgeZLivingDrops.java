package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import vazkii.zeta.event.ZLivingDrops;

import java.util.Collection;

public class ForgeZLivingDrops implements ZLivingDrops {
    private final LivingDropsEvent e;

    public ForgeZLivingDrops(LivingDropsEvent e) {
        this.e = e;
    }

    @Override
    public LivingEntity getEntity() {
        return e.getEntity();
    }

    @Override
    public Collection<ItemEntity> getDrops() {
        return e.getDrops();
    }
}
