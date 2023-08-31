package vazkii.quark.content.tweaks.module;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.caelus.api.CaelusApi;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class BetterElytraRocketModule extends QuarkModule {

	@SubscribeEvent
	public void onUseRocket(PlayerInteractEvent.RightClickItem event) {
		Player player = event.getEntity();

        boolean canPlayerElytraFly = ModList.get().isLoaded("caelus") ?
                CaelusApi.getInstance().canFly(player) : player.getItemBySlot(EquipmentSlot.CHEST).canElytraFly(player);

		if (!player.isFallFlying() && canPlayerElytraFly) {
			Level world = player.level;
			ItemStack itemstack = event.getItemStack();

			if(itemstack.getItem() instanceof FireworkRocketItem) {
				if(!world.isClientSide) {
					world.addFreshEntity(new FireworkRocketEntity(world, itemstack, player));
					if(!player.getAbilities().instabuild)
						itemstack.shrink(1);
				}

				player.startFallFlying();
				player.jumpFromGround();

				event.setCanceled(true);
				event.setCancellationResult(world.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME);
			}

		}

	}

}
