package org.violetmoon.quark.content.tools.entity.rang;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.violetmoon.quark.content.tools.config.PickarangType;
import org.violetmoon.quark.content.tools.module.PickarangModule;

public class Flamerang extends AbstractPickarang<Flamerang> {

	public Flamerang(EntityType<Flamerang> type, Level worldIn) {
		super(type, worldIn);
	}

	public Flamerang(EntityType<Flamerang> type, Level worldIn, Player thrower) {
		super(type, worldIn, thrower);
	}

	@Override
	protected void emitParticles(Vec3 pos, Vec3 ourMotion) {
		if(Math.random() < 0.4)
			this.level().addParticle(ParticleTypes.FLAME,
					pos.x - ourMotion.x * 0.25D + (Math.random() - 0.5) * 0.4,
					pos.y - ourMotion.y * 0.25D + (Math.random() - 0.5) * 0.4,
					pos.z - ourMotion.z * 0.25D + (Math.random() - 0.5) * 0.4,
					(Math.random() - 0.5) * 0.1,
					(Math.random() - 0.5) * 0.1,
					(Math.random() - 0.5) * 0.1);
	}

	@Override
	public PickarangType<Flamerang> getPickarangType() {
		return PickarangModule.flamerangType;
	}

}
