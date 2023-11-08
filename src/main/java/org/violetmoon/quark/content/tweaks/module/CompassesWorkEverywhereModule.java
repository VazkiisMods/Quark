package org.violetmoon.quark.content.tweaks.module;

import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.content.tweaks.client.item.ClockTimeGetter;
import org.violetmoon.quark.content.tweaks.client.item.CompassAngleGetter;
import org.violetmoon.zeta.client.event.load.ZClientSetup;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.bus.PlayEvent;
import org.violetmoon.zeta.event.play.entity.player.ZPlayerTick;
import org.violetmoon.zeta.event.play.loading.ZGatherHints;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@ZetaLoadModule(category = "tweaks")
public class CompassesWorkEverywhereModule extends ZetaModule {

	@Config public static boolean enableCompassNerf = true;
	@Config(flag = "clock_nerf") 
	public static boolean enableClockNerf = true;

	@Config public static boolean enableNether = true;
	@Config public static boolean enableEnd = true;
	
	@Hint("clock_nerf") Item clock = Items.CLOCK;

	@PlayEvent
	public void addAdditionalHints(ZGatherHints consumer) {
		if(!enableNether && !enableEnd && !enableCompassNerf)
			return;
		
		MutableComponent comp = Component.literal("");
		String pad = "";
		if(enableNether) {
			comp = comp.append(pad).append(Component.translatable("quark.jei.hint.compass_nether"));
			pad = " ";
		}
		if(enableEnd) {
			comp = comp.append(pad).append(Component.translatable("quark.jei.hint.compass_end"));
			pad = " ";
		}
		if(enableCompassNerf)
			comp = comp.append(pad).append(Component.translatable("quark.jei.hint.compass_nerf"));
		
		consumer.accept(Items.COMPASS, comp);
	}

	@PlayEvent
	public void onUpdate(ZPlayerTick.Start event) {
		Inventory inventory = event.getPlayer().getInventory();
		for(int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if(stack.getItem() == Items.COMPASS)
				CompassAngleGetter.tickCompass(event.getPlayer(), stack);
			else if(stack.getItem() == Items.CLOCK)
				ClockTimeGetter.tickClock(stack);
		}
	}

	@ZetaLoadModule(clientReplacement = true)
	public static class Client extends CompassesWorkEverywhereModule {

		@LoadEvent
		public void clientSetup(ZClientSetup e) {
			e.enqueueWork(() -> {
				if(!enabled)
					return;

				if(enableCompassNerf || enableNether || enableEnd)
					ItemProperties.register(Items.COMPASS, new ResourceLocation("angle"), new CompassAngleGetter.Impl());

				if(enableClockNerf)
					ItemProperties.register(Items.CLOCK, new ResourceLocation("time"), new ClockTimeGetter.Impl());
			});
		}

	}

}
