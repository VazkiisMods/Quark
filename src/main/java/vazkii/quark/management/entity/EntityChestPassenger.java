package vazkii.quark.management.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class EntityChestPassenger extends Entity implements IInventory {

	private NonNullList<ItemStack> items = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
	
    private static final DataParameter<ItemStack> CHEST_TYPE = EntityDataManager.<ItemStack>createKey(EntityChestPassenger.class, DataSerializers.ITEM_STACK);
    private static final String TAG_CHEST_TYPE = "chestType";
    
	public EntityChestPassenger(World worldIn) {
		super(worldIn);
	}
	
	public EntityChestPassenger(World worldIn, ItemStack stack) {
		this(worldIn);
		
		ItemStack newStack = stack.copy();
		newStack.setCount(1);
		dataManager.set(CHEST_TYPE, newStack);
	}

	@Override
	protected void entityInit() {
		noClip = true;
		dataManager.register(CHEST_TYPE, new ItemStack(Blocks.CHEST));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(isDead)
			return;
		
		if(!isRiding()) {
			if(!world.isRemote)
				setDead();
			
			return;
		}
		
		Entity riding = getRidingEntity();
		rotationYaw = riding.prevRotationYaw;
		rotationPitch = 0F;
	}

	@Override
	public int getSizeInventory() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack itemstack : items)
			if(!itemstack.isEmpty())
				return false;

		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return items.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ItemStackHelper.getAndSplit(items, index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack itemstack = items.get(index);

		if(itemstack.isEmpty())
			return ItemStack.EMPTY;
		else {
			items.set(index, ItemStack.EMPTY);
			return itemstack;
		}
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		items.set(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		// NO-OP
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return !isDead && player.getDistanceSq(this) <= 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// NO-OP
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// NO-OP
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// NO-OP
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		items.clear();
	}

	@Override
	public void setDead() {
		if(!world.isRemote) {
			InventoryHelper.dropInventoryItems(world, this, this);
			InventoryHelper.spawnItemStack(world, posX, posY, posZ, getChestType());
		}
		
		super.setDead();
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		ItemStackHelper.saveAllItems(compound, items);
		
		NBTTagCompound itemCmp = new NBTTagCompound();
		dataManager.get(CHEST_TYPE).writeToNBT(itemCmp);
		compound.setTag(TAG_CHEST_TYPE, itemCmp);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		ItemStackHelper.loadAllItems(compound, items);
		
		NBTTagCompound itemCmp = compound.getCompoundTag(TAG_CHEST_TYPE);
		ItemStack stack = new ItemStack(itemCmp);
		if(!stack.isEmpty())
			dataManager.set(CHEST_TYPE, stack);
	}
	
	public ItemStack getChestType() {
		return dataManager.get(CHEST_TYPE);
	}

}
