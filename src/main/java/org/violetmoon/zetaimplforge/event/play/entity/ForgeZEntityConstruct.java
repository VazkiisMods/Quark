package org.violetmoon.zetaimplforge.event.play.entity;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import org.violetmoon.zeta.event.play.entity.ZEntityConstruct;

public class ForgeZEntityConstruct implements ZEntityConstruct {
	private final EntityEvent.EntityConstructing e;

	public ForgeZEntityConstruct(EntityEvent.EntityConstructing e) {
		this.e = e;
	}

	@Override
	public Entity getEntity() {
		return e.getEntity();
	}
}
