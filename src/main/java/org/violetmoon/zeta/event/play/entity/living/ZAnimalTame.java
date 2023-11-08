package org.violetmoon.zeta.event.play.entity.living;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import org.violetmoon.zeta.event.bus.IZetaPlayEvent;

public interface ZAnimalTame extends IZetaPlayEvent {
    Animal getAnimal();
    Player getTamer();
}
