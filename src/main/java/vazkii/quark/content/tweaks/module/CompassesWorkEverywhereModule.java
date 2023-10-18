package vazkii.quark.content.tweaks.module;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tweaks.client.item.ClockTimeGetter;
import vazkii.quark.content.tweaks.client.item.CompassAngleGetter;
import vazkii.zeta.event.ZGatherHints;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class CompassesWorkEverywhereModule extends QuarkModule {

	@Config public static boolean enableCompassNerf = true;
	@Config(flag = "clock_nerf") 
	public static boolean enableClockNerf = true;

	@Config public static boolean enableNether = true;
	@Config public static boolean enableEnd = true;
	
	@Hint("clock_nerf") Item clock = Items.CLOCK;

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void clientSetup(ZClientSetup e) {
		// register = addPropertyOverride
		if(enabled && (enableCompassNerf || enableNether || enableEnd))
			e.enqueueWork(() -> ItemProperties.register(Items.COMPASS, new ResourceLocation("angle"), new CompassAngleGetter.Impl()));

		if(enabled && enableClockNerf)
			e.enqueueWork(() -> ItemProperties.register(Items.CLOCK, new ResourceLocation("time"), new ClockTimeGetter.Impl()));
	}

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

	@SubscribeEvent
	public void onUpdate(PlayerTickEvent event) {
		if(event.phase == Phase.START) {
			Inventory inventory = event.player.getInventory();
			for(int i = 0; i < inventory.getContainerSize(); i++) {
				ItemStack stack = inventory.getItem(i);
				if(stack.getItem() == Items.COMPASS)
					CompassAngleGetter.tickCompass(event.player, stack);
				else if(stack.getItem() == Items.CLOCK)
					ClockTimeGetter.tickClock(stack);
			}
		}
	}

}
