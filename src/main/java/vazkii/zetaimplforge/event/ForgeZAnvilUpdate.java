package vazkii.zetaimplforge.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import vazkii.zeta.event.ZAnvilUpdate;

public class ForgeZAnvilUpdate implements ZAnvilUpdate {
    private final AnvilUpdateEvent e;

    public ForgeZAnvilUpdate(AnvilUpdateEvent e) {
        this.e = e;
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
    public ItemStack getOutput() {
        return e.getOutput();
    }

    @Override
    public void setOutput(ItemStack output) {
        e.setOutput(output);
    }

    @Override
    public void setCost(int cost) {
        e.setCost(cost);
    }

    public static class Lowest extends ForgeZAnvilUpdate implements ZAnvilUpdate.Lowest {
        public Lowest(AnvilUpdateEvent e) {
            super(e);
        }
    }
}
