package vazkii.zetaimplforge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import vazkii.zeta.event.ZAnvilRepair;

public class ForgeZAnvilRepair implements ZAnvilRepair {
    private final AnvilRepairEvent e;

    public ForgeZAnvilRepair(AnvilRepairEvent e) {
        this.e = e;
    }

    @Override
    public Player getEntity() {
        return e.getEntity();
    }

    @Override
    public ItemStack getOutput() {
        return e.getOutput();
    }

    @Override
    public ItemStack getLeft() {
        return e.getLeft();
    }

    @Override
    public ItemStack getRight() {
        return e.getRight();
    }

    @Override
    public float getBreakChance() {
        return e.getBreakChance();
    }

    @Override
    public void setBreakChance(float breakChance) {
        e.setBreakChance(breakChance);
    }
}
