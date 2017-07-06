package vazkii.quark.misc.feature;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.misc.item.ItemEnderdragonScale;
import vazkii.quark.misc.recipe.ElytraDuplicationRecipe;

public class EnderdragonScales extends Feature {

	public static Item enderdragonScale;
	
	public static int required;
	public static boolean dyeBlack;
	int dropped;
	
	@Override
	public void setupConfig() {
		required = loadPropInt("Required Scales per Elytra", "", 1);
		dropped = loadPropInt("Amount Dropped per Dragon Kill", "", 1);
		dyeBlack = loadPropBool("Dye Elytra Black", "Should the crafted Elytra be dyed black? (only works if Dyed Elytras from Vanity is loaded)", true);
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		enderdragonScale = new ItemEnderdragonScale();
		
		new ElytraDuplicationRecipe();
	}
	
	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityDragon && !event.getEntity().getEntityWorld().isRemote) {
			EntityDragon dragon = (EntityDragon) event.getEntity();

			if(dragon.getFightManager() && dragon.getFightManager().hasPreviouslyKilledDragon() && dragon.deathTicks == 100) {
				EntityItem item = new EntityItem(dragon.world, dragon.posX, dragon.posY, dragon.posZ, new ItemStack(enderdragonScale, dropped));
				dragon.world.spawnEntity(item);
			}
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
}
