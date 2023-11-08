package vazkii.quark.content.tweaks.module;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Vex;
import vazkii.zeta.event.ZLivingTick;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

@ZetaLoadModule(category = "tweaks")
public class VexesDieWithTheirMastersModule extends ZetaModule {

	@PlayEvent // omae wa mou shindeiru
	public void checkWhetherAlreadyDead(ZLivingTick event) {
		if (event.getEntity() instanceof Vex vex) {
			Mob owner = vex.getOwner();
			if (owner != null && owner.isDeadOrDying() && !vex.isDeadOrDying())
				vex.hurt(DamageSource.mobAttack(owner).bypassArmor().bypassInvul().bypassMagic(), vex.getHealth());
		}
	}
}
