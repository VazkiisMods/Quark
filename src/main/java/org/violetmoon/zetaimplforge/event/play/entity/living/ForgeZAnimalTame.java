package org.violetmoon.zetaimplforge.event.play.entity.living;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import org.violetmoon.zeta.event.play.entity.living.ZAnimalTame;

public class ForgeZAnimalTame implements ZAnimalTame {
    private final AnimalTameEvent e;

    public ForgeZAnimalTame(AnimalTameEvent e) {
        this.e = e;
    }

    @Override
    public Animal getAnimal() {
        return e.getAnimal();
    }

    @Override
    public Player getTamer() {
        return e.getTamer();
    }
}
