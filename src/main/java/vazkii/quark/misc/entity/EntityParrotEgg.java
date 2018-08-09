package vazkii.quark.misc.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityParrotEgg extends EntityThrowable {

	private static final DataParameter<Integer> COLOR = EntityDataManager.<Integer>createKey(EntityParrotEgg.class, DataSerializers.VARINT);
	private static final String TAG_COLOR = "color";

	public EntityParrotEgg(World worldIn) {
		super(worldIn);
	}

	public EntityParrotEgg(World worldIn, EntityLivingBase throwerIn, int color) {
		super(worldIn, throwerIn);
		dataManager.set(COLOR, color);
	}

	@Override
	protected void entityInit() {
		super.entityInit();

		dataManager.register(COLOR, 0);	
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger(TAG_COLOR, getColor());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		dataManager.set(COLOR, compound.getInteger(TAG_COLOR));
	}

	public int getColor() {
		return dataManager.get(COLOR);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleStatusUpdate(byte id) {
		if(id == 3) {
			double d0 = 0.08D;

			for(int i = 0; i < 8; ++i)
				this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, Item.getIdFromItem(Items.EGG));
		}
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if(result.entityHit != null)
			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), 0.0F);

		if(!world.isRemote) {
			EntityParrot parrot = new EntityParrot(world);
			parrot.setVariant(getColor());
			parrot.setGrowingAge(-24000);
			parrot.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0.0F);
			world.spawnEntity(parrot);

			world.setEntityState(this, (byte) 3);
			setDead();
		}
	}

}
