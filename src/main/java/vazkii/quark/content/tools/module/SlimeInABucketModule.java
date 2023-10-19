package vazkii.quark.content.tools.module;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;
import vazkii.zeta.util.ItemNBTHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tools.item.SlimeInABucketItem;

@LoadModule(category = "tools", hasSubscriptions = true)
public class SlimeInABucketModule extends QuarkModule {

	@Hint public static Item slime_in_a_bucket;

	@LoadEvent
	public final void register(ZRegister event) {
		slime_in_a_bucket = new SlimeInABucketItem(this);
	}

	@LoadEvent
	@OnlyIn(Dist.CLIENT)
	public void clientSetup(ZClientSetup event) {
		event.enqueueWork(() -> ItemProperties.register(slime_in_a_bucket, new ResourceLocation("excited"),
				(stack, world, e, id) -> ItemNBTHelper.getBoolean(stack, SlimeInABucketItem.TAG_EXCITED, false) ? 1 : 0));
	}

	@SubscribeEvent
	public void entityInteract(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() != null) {
			if(event.getTarget().getType() == EntityType.SLIME && ((Slime) event.getTarget()).getSize() == 1 && event.getTarget().isAlive()) {
				Player player = event.getEntity();
				InteractionHand hand = InteractionHand.MAIN_HAND;
				ItemStack stack = player.getMainHandItem();
				if(stack.isEmpty() || stack.getItem() != Items.BUCKET) {
					stack = player.getOffhandItem();
					hand = InteractionHand.OFF_HAND;
				}

				if(!stack.isEmpty() && stack.getItem() == Items.BUCKET) {
					if(!event.getLevel().isClientSide) {
						ItemStack outStack = new ItemStack(slime_in_a_bucket);
						CompoundTag cmp = event.getTarget().serializeNBT();
						ItemNBTHelper.setCompound(outStack, SlimeInABucketItem.TAG_ENTITY_DATA, cmp);
						
						if(stack.getCount() == 1)
							player.setItemInHand(hand, outStack);
						else {
							stack.shrink(1);
							if(stack.getCount() == 0)
								player.setItemInHand(hand, outStack);
							else if(!player.getInventory().add(outStack))
								player.drop(outStack, false);
						}

			            event.getLevel().gameEvent(player, GameEvent.ENTITY_INTERACT, event.getTarget().position());
						event.getTarget().discard();
					}
					else player.swing(hand);
					
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.SUCCESS);
				}
			}
		}
	}

}
