package org.violetmoon.quark.content.tools.entity.rang;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import org.violetmoon.quark.content.tools.config.PickarangType;
import org.violetmoon.quark.content.tools.module.PickarangModule;

public class Pickarang extends AbstractPickarang<Pickarang> {

	public Pickarang(EntityType<Pickarang> type, Level worldIn) {
		super(type, worldIn);
	}

	public Pickarang(EntityType<Pickarang> type, Level worldIn, LivingEntity throwerIn) {
		super(type, worldIn, throwerIn);
	}

	@Override
	public PickarangType<Pickarang> getPickarangType() {
		return PickarangModule.pickarangType;
	}

}
