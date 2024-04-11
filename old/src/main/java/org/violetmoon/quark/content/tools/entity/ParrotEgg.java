package org.violetmoon.quark.content.tools.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.tools.module.ParrotEggsModule;

public class ParrotEgg extends ThrowableItemProjectile {
	public static final int VARIANTS = 5;

	protected static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ParrotEgg.class, EntityDataSerializers.INT);

	private static final int EVENT_BREAK = 3;

	public ParrotEgg(EntityType<ParrotEgg> entityType, Level world) {
		super(entityType, world);
	}

	public ParrotEgg(Level world, double x, double y, double z) {
		super(ParrotEggsModule.parrotEggType, x, y, z, world);
	}

	public ParrotEgg(Level world, LivingEntity owner) {
		super(ParrotEggsModule.parrotEggType, owner, world);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		getEntityData().define(COLOR, 0);
	}

	//Todo: This COULD just return an enum reference, but Im too lazy to do that rn
	public int getVariant() {
		return Mth.clamp(getEntityData().get(COLOR), 0, VARIANTS - 1);
	}

	public void setVariant(int variant) {
		getEntityData().set(COLOR, Mth.clamp(variant, 0, VARIANTS - 1));
	}

	@NotNull
	@Override
	protected Item getDefaultItem() {
		return ParrotEggsModule.parrotEggs.get(getVariant());
	}

	@Override
	public void handleEntityEvent(byte id) {
		if(id == EVENT_BREAK) {
			Vec3 pos = position();

			double motion = 0.08;

			for(int i = 0; i < 8; ++i)
				level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), pos.x, pos.y, pos.z, (random.nextFloat() - 0.5) * motion, (random.nextFloat() - 0.5) * motion, (random.nextFloat() - 0.5) * motion);
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
		super.onHitEntity(entityHitResult);
		entityHitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
	}

	@Override
	protected void onHit(@NotNull HitResult hitResult) {
		super.onHit(hitResult);
		if(!this.level().isClientSide) {
			Parrot parrot = EntityType.PARROT.create(level());
			if(parrot != null) {
				parrot.setVariant(Parrot.Variant.byId(getVariant()));
				parrot.setAge(-24000);
				parrot.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
				level().addFreshEntity(parrot);
			}

			this.level().broadcastEntityEvent(this, (byte) EVENT_BREAK);
			this.discard();
		}
	}

}
