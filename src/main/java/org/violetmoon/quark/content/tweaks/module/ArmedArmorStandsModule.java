package org.violetmoon.quark.content.tweaks.module;

import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.entity.ZEntityConstruct;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

/**
 * @author WireSegal
 * Created at 8:40 AM on 8/27/19.
 */
@ZetaLoadModule(category = "tweaks")
public class ArmedArmorStandsModule extends ZetaModule {

	@Hint Item armor_stand = Items.ARMOR_STAND;

	@PlayEvent
	public void entityConstruct(ZEntityConstruct event) {
		if(event.getEntity() instanceof ArmorStand stand) {
			if(!stand.isShowArms())
				setShowArms(stand, true);
		}
	}

	private void setShowArms(ArmorStand e, boolean showArms) {
		e.getEntityData().set(ArmorStand.DATA_CLIENT_FLAGS, setBit(e.getEntityData().get(ArmorStand.DATA_CLIENT_FLAGS), 4, showArms));
	}

	private byte setBit(byte status, int bitFlag, boolean value) {
		if (value)
			status = (byte)(status | bitFlag);
		else
			status = (byte)(status & ~bitFlag);

		return status;
	}
}
