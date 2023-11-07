package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import vazkii.zeta.event.ZEntityItemPickup;

public class ForgeZEntityItemPickup implements ZEntityItemPickup {
    private final EntityItemPickupEvent e;

    public ForgeZEntityItemPickup(EntityItemPickupEvent e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    @Override
    public ItemEntity getItem() {
        return e.getItem();
    }
}
