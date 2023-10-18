package vazkii.quark.content.tweaks.module;

import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;

/**
 * @author WireSegal
 * Created at 8:40 AM on 8/27/19.
 */
@LoadModule(category = "tweaks", hasSubscriptions = true)
public class ArmedArmorStandsModule extends QuarkModule {

	@Hint Item armor_stand = Items.ARMOR_STAND;

	@SubscribeEvent
	public void entityConstruct(EntityEvent.EntityConstructing event) {
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
