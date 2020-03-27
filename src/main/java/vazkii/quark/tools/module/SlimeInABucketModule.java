package vazkii.quark.tools.module;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.tools.item.SlimeInABucketItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class SlimeInABucketModule extends Module {

	public static Item slime_in_a_bucket;
	
	@Override
	public void construct() {
		slime_in_a_bucket = new SlimeInABucketItem(this);
	}
	
	@SubscribeEvent
	public void entityInteract(PlayerInteractEvent.EntityInteract event) {
		if(event.getTarget() != null && !event.getWorld().isRemote) {
			if(event.getTarget().getType() == EntityType.SLIME && ((SlimeEntity) event.getTarget()).getSlimeSize() == 1 && event.getTarget().isAlive()) {
				PlayerEntity player = event.getPlayer();
				Hand hand = Hand.MAIN_HAND;
				ItemStack stack = player.getHeldItemMainhand();
				if(stack.isEmpty() || stack.getItem() != Items.BUCKET) {
					stack = player.getHeldItemOffhand();
					hand = Hand.OFF_HAND;
				}

				if(!stack.isEmpty() && stack.getItem() == Items.BUCKET) {
					ItemStack outStack = new ItemStack(slime_in_a_bucket);
					CompoundNBT cmp = event.getTarget().serializeNBT();
					ItemNBTHelper.setCompound(outStack, SlimeInABucketItem.TAG_ENTITY_DATA, cmp);
					
					if(stack.getCount() == 1)
						player.setHeldItem(hand, outStack);
					else {
						stack.shrink(1);
						if(stack.getCount() == 0)
							player.setHeldItem(hand, outStack);
						else if(!player.inventory.addItemStackToInventory(outStack))
							player.dropItem(outStack, false);
					}

					player.swingArm(hand);
					event.getTarget().remove();
				}
			}
		}
	}
	
}
