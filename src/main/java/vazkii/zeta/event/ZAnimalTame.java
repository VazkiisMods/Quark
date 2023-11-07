package vazkii.zeta.event;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import vazkii.zeta.event.bus.IZetaPlayEvent;

public interface ZAnimalTame extends IZetaPlayEvent {
    Animal getAnimal();
    Player getTamer();
}
