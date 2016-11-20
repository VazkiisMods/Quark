/**
 * This class was created by <Exidex>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [17/11/2016, 17:20:00 (GMT)]
 */

package vazkii.quark.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SwordAttackBehindGrass extends Feature{

	private List<Class<?>> classes;

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		classes = new ArrayList<>();

		try {
			if(Loader.isModLoaded("tconstruct")) {
				//TConstract version: 2.5.0 or above
				classes.add(Class.forName("slimeknights.tconstruct.library.tools.SwordCore"));
			}
		} catch(ClassNotFoundException e) {
			// Class doesn't exist. Do nothing.
		}
	}

	@SuppressWarnings("UnusedDeclaration")
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent.LeftClickBlock event) {
		if(event.getEntityPlayer().getHeldItemMainhand() == null)
			return;

		IBlockState state = event.getWorld().getBlockState(event.getPos()).getActualState(event.getWorld(), event.getPos());
		Block block = state.getBlock();
		if(block instanceof BlockBush) {
			Item item = event.getEntityPlayer().getHeldItemMainhand().getItem();
			if(item instanceof ItemSword || this.isItemInstanceOf(item, classes)) {
				EntityPlayer entityPlayer = event.getEntityPlayer();
				//Player block reach distance
				int length = 4;

				Vec3d startPos = new Vec3d(entityPlayer.posX, entityPlayer.posY + entityPlayer.getEyeHeight(), entityPlayer.posZ);
				Vec3d endPos = startPos.add(new Vec3d(entityPlayer.getLookVec().xCoord * length, entityPlayer.getLookVec().yCoord * length, entityPlayer.getLookVec().zCoord * length));
				AxisAlignedBB axisAlignedBB = new AxisAlignedBB(startPos, endPos).expandXyz(1.0D);

				Entity entity = rayTraceEntityExcluding(entityPlayer, event.getWorld(), axisAlignedBB, startPos, endPos);
				if(entity != null) {
					event.getEntityPlayer().attackTargetEntityWithCurrentItem(entity);
					event.setCanceled(true);
				}
			}
		}
	}

	private boolean isItemInstanceOf(Item item, List<Class<?>> list) {
		for(Class<?> clazz : list ) {
			if(clazz.isInstance(item)) return true;
		}
		return false;
	}

	private Entity rayTraceEntityExcluding(@Nullable Entity entityIn, World world, AxisAlignedBB axisAlignedBB, Vec3d startPos, Vec3d endPos) {
		Entity entity = null;
		List<Entity> list = world.getEntitiesInAABBexcluding(entityIn, axisAlignedBB, null);
		double d0 = 0.0D;

		for(Entity entity1 : list) {
			axisAlignedBB = entity1.getEntityBoundingBox().expandXyz(0.30000001192092896D);
			RayTraceResult raytraceResult = axisAlignedBB.calculateIntercept(startPos, endPos);

			if(raytraceResult != null) {
				double d1 = startPos.squareDistanceTo(raytraceResult.hitVec);

				if(d1 < d0 || d0 == 0.0D) {
					entity = entity1;
					d0 = d1;
				}
			}
		}
		return entity;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

}
