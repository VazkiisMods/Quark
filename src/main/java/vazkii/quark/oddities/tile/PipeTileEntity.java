package vazkii.quark.oddities.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.annotation.Nonnull;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import vazkii.arl.block.tile.TileSimpleInventory;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.oddities.block.PipeBlock;
import vazkii.quark.oddities.module.PipesModule;

public class PipeTileEntity extends TileSimpleInventory implements ITickableTileEntity {

	public PipeTileEntity() {
		super(PipesModule.tileEntityType);
	}
	private static final String TAG_PIPE_ITEMS = "pipeItems";

	private boolean needsSync = false;
	private boolean iterating = false;
	public final List<PipeItem> pipeItems = new LinkedList<>();
	public final List<PipeItem> queuedItems = new LinkedList<>();

	public static boolean isTheGoodDay(World world) {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1 == 4 && calendar.get(Calendar.DAY_OF_MONTH) == 1;
	}

	@Override
	public void tick() {
		if(!isPipeEnabled() && world.getGameTime() % 10 == 0 && world instanceof ServerWorld) 
			((ServerWorld) world).spawnParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, 1.0F), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.2, 0.2, 0.2, 0);

		BlockState blockAt = world.getBlockState(pos);
		if (isPipeEnabled() && blockAt.getBlock() instanceof PipeBlock) {
			for (Direction side : Direction.values()) {
				if (!world.isRemote && PipeBlock.getType(blockAt, side) == null) {
					double minX = pos.getX() + 0.25 + 0.5 * Math.min(0, side.getXOffset());
					double minY = pos.getY() + 0.25 + 0.5 * Math.min(0, side.getYOffset());
					double minZ = pos.getZ() + 0.25 + 0.5 * Math.min(0, side.getZOffset());
					double maxX = pos.getX() + 0.75 + 0.5 * Math.max(0, side.getXOffset());
					double maxY = pos.getY() + 0.75 + 0.5 * Math.max(0, side.getYOffset());
					double maxZ = pos.getZ() + 0.75 + 0.5 * Math.max(0, side.getZOffset());

					Direction opposite = side.getOpposite();

					boolean any = false;
					Predicate<ItemEntity> predicate = entity -> {
						if(entity == null || !entity.isAlive())
							return false;
						
						Vec3d motion = entity.getMotion();
						Direction dir = Direction.getFacingFromVector(motion.x, motion.y, motion.z);
						
						return dir == opposite;
					};
					
					for (ItemEntity item : world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ), predicate)) {
						passIn(item.getItem().copy(), side);
						
						if (PipesModule.doPipesWhoosh) { 
							if (isTheGoodDay(world))
								world.playSound(null, item.getPosX(), item.getPosY(), item.getPosZ(), QuarkSounds.BLOCK_PIPE_PICKUP_LENNY, SoundCategory.BLOCKS, 1f, 1f);
							else
								world.playSound(null, item.getPosX(), item.getPosY(), item.getPosZ(), QuarkSounds.BLOCK_PIPE_PICKUP, SoundCategory.BLOCKS, 1f, 1f);
						}

						any = true;
						item.remove();
					}

					if (any)
						sync();
				}
			}
		}

		int currentOut = getComparatorOutput();

		if(!pipeItems.isEmpty()) {
			if(PipesModule.maxPipeItems > 0 && pipeItems.size() > PipesModule.maxPipeItems && !world.isRemote) {
				world.playEvent(2001, pos, Block.getStateId(world.getBlockState(pos)));
				dropItem(new ItemStack(getBlockState().getBlock()));
				world.removeBlock(getPos(), false);
			}

			ListIterator<PipeItem> itemItr = pipeItems.listIterator();
			iterating = true;
			needsSync = false;
			while(itemItr.hasNext()) {
				PipeItem item = itemItr.next();
				Direction lastFacing = item.outgoingFace;
				if(item.tick(this)) {
					needsSync = true;
					itemItr.remove();

					if (item.valid)
						passOut(item);
					else {
						dropItem(item.stack, lastFacing, true);
					}
				}
			}
			iterating = false;

			pipeItems.addAll(queuedItems);
			if(needsSync || !queuedItems.isEmpty())
				sync();
			needsSync = false;
			queuedItems.clear();
		}

		if(getComparatorOutput() != currentOut)
			world.updateComparatorOutputLevel(getPos(), getBlockState().getBlock());
	}

	public int getComparatorOutput() {
		return Math.min(15, pipeItems.size());
	}

	public Iterator<PipeItem> getItemIterator() {
		return pipeItems.iterator(); 
	}

	public boolean passIn(ItemStack stack, Direction face, long seed, int time) {
		PipeItem item = new PipeItem(stack, face, seed);
		if(!iterating) {
			int currentOut = getComparatorOutput();
			pipeItems.add(item);
			item.timeInWorld = time;
			if(getComparatorOutput() != currentOut)
				world.updateComparatorOutputLevel(getPos(), getBlockState().getBlock());
		} else queuedItems.add(item);

		return true;
	}

	public boolean passIn(ItemStack stack, Direction face) {
		return passIn(stack, face, world.rand.nextLong(), 0);
	}

	protected void passOut(PipeItem item) {
		BlockPos targetPos = getPos().offset(item.outgoingFace);
		TileEntity tile = world.getTileEntity(targetPos);
		boolean did = false;
		if(tile != null) {
			if(tile instanceof PipeTileEntity)
				did = ((PipeTileEntity) tile).passIn(item.stack, item.outgoingFace.getOpposite(), item.rngSeed, item.timeInWorld);
			else if (!world.isRemote) {
				ItemStack result = putIntoInv(item.stack, tile, item.outgoingFace.getOpposite(), false);
				if(result.getCount() != item.stack.getCount()) {
					did = true;
					if(!result.isEmpty())
						bounceBack(item, result);
				}
			}
		}

		if(!did)
			bounceBack(item, null);
	}

	private void bounceBack(PipeItem item, ItemStack stack) {
		if(!world.isRemote)
			passIn(stack == null ? item.stack : stack, item.outgoingFace, item.rngSeed, item.timeInWorld);
	}

	public void dropItem(ItemStack stack) {
		dropItem(stack, null, false);
	}

	public void dropItem(ItemStack stack, Direction facing, boolean playSound) {
		if(!world.isRemote) {
			double posX = pos.getX() + 0.5;
			double posY = pos.getY() + 0.25;
			double posZ = pos.getZ() + 0.5;

			if (facing != null) {
				posX -= facing.getXOffset() * 0.4;
				posY -= facing.getYOffset() * 0.65;
				posZ -= facing.getZOffset() * 0.4;
			}

			boolean shootOut = isPipeEnabled();

			float pitch = 1f;
			if (!shootOut)
				pitch = 0.025f;

			if (playSound && PipesModule.doPipesWhoosh) { 
				if (isTheGoodDay(world))
					world.playSound(null, posX, posY, posZ, QuarkSounds.BLOCK_PIPE_SHOOT_LENNY, SoundCategory.BLOCKS, 1f, pitch);
				else
					world.playSound(null, posX, posY, posZ, QuarkSounds.BLOCK_PIPE_SHOOT, SoundCategory.BLOCKS, 1f, pitch);
			}

			ItemEntity entity = new ItemEntity(world, posX, posY, posZ, stack);
			entity.setDefaultPickupDelay();

			double velocityMod = 0.5;
			if (!shootOut)
				velocityMod = 0.125;

			if (facing != null) {
				double mx = -facing.getXOffset() * velocityMod;
				double my = -facing.getYOffset() * velocityMod;
				double mz = -facing.getZOffset() * velocityMod;
				entity.setMotion(mx, my, mz);
			}
			world.addEntity(entity);
		}
	}

	public void dropAllItems() {
		for(PipeItem item : pipeItems)
			dropItem(item.stack);
		pipeItems.clear();
	}

	@Override
	public void readSharedNBT(CompoundNBT cmp) {
		super.readSharedNBT(cmp);

		ListNBT pipeItemList = cmp.getList(TAG_PIPE_ITEMS, cmp.getId());
		pipeItems.clear();
		pipeItemList.forEach(listCmp -> {
			PipeItem item = PipeItem.readFromNBT((CompoundNBT) listCmp);
			pipeItems.add(item);
		});
	}

	@Override
	public void writeSharedNBT(CompoundNBT cmp) {
		super.writeSharedNBT(cmp);

		ListNBT pipeItemList = new ListNBT();
		for(PipeItem item : pipeItems) {
			CompoundNBT listCmp = new CompoundNBT();
			item.writeToNBT(listCmp);
			pipeItemList.add(listCmp);
		}
		cmp.put(TAG_PIPE_ITEMS, pipeItemList);
	}

	protected boolean canFit(ItemStack stack, BlockPos pos, Direction face) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null)
			return false;

		if(tile instanceof PipeTileEntity)
			return ((PipeTileEntity) tile).isPipeEnabled();
		else {
			ItemStack result = putIntoInv(stack, tile, face, true);
			return result.isEmpty();
		}
	}

	protected boolean isPipeEnabled() {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() instanceof PipeBlock && state.get(PipeBlock.ENABLED);
	}

	protected ItemStack putIntoInv(ItemStack stack, TileEntity tile, Direction face, boolean simulate) {
		IItemHandler handler = null;
		
		LazyOptional<IItemHandler> opt = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face); 
		if(opt.isPresent())
			handler = opt.orElse(null);
		else if(tile instanceof ISidedInventory)
			handler = new SidedInvWrapper((ISidedInventory) tile, face);
		else if(tile instanceof IInventory)
			handler = new InvWrapper((IInventory) tile);

		if(handler != null)
			return ItemHandlerHelper.insertItem(handler, stack, simulate);
		return stack;
	}

	@Override
	public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull Direction direction) {
		return index == direction.ordinal() && isPipeEnabled();
	}

	@Override
	public void setInventorySlotContents(int i, @Nonnull ItemStack itemstack) {
		if(!itemstack.isEmpty()) {
			Direction side = Direction.values()[i];
			passIn(itemstack, side);
			sync();
		}
	}

	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	protected boolean needsToSyncInventory() {
		return true;
	}
	
	public void sync() {
		SUpdateTileEntityPacket packet = getUpdatePacket();
		
		if(packet != null && world instanceof ServerWorld) {
			((ServerChunkProvider) world.getChunkProvider()).chunkManager
					.getTrackingPlayers(new ChunkPos(pos), false)
					.forEach(e -> e.connection.sendPacket(packet));
		}
	}

	public static class PipeItem {

		private static final String TAG_TICKS = "ticksInPipe";
		private static final String TAG_INCOMING = "incomingFace";
		private static final String TAG_OUTGOING = "outgoingFace";
		private static final String TAG_RNG_SEED = "rngSeed";
		private static final String TAG_TIME_IN_WORLD = "timeInWorld";

		private static final List<Direction> HORIZONTAL_SIDES_LIST = Arrays.asList(MiscUtil.HORIZONTALS);

		public final ItemStack stack;
		public int ticksInPipe;
		public final Direction incomingFace;
		public Direction outgoingFace;
		public long rngSeed;
		public int timeInWorld = 0;
		public boolean valid = true;

		public PipeItem(ItemStack stack, Direction face, long rngSeed) {
			this.stack = stack;
			ticksInPipe = 0;
			incomingFace = outgoingFace = face;
			this.rngSeed = rngSeed;
		}

		protected boolean tick(PipeTileEntity pipe) {
			ticksInPipe++;
			timeInWorld++;

			if(!pipe.world.isRemote && ticksInPipe == PipesModule.pipeSpeed / 2 - 1) {
				Direction target = getTargetFace(pipe);
				if (outgoingFace != target)
					pipe.needsSync = true;
				outgoingFace = target;
			}

			if(outgoingFace == null) {
				valid = false;
				return true;
			}

			return ticksInPipe >= PipesModule.pipeSpeed;
		}

		protected Direction getTargetFace(PipeTileEntity pipe) {
			BlockPos pipePos = pipe.getPos();
			if(incomingFace != Direction.DOWN && pipe.canFit(stack, pipePos.offset(Direction.DOWN), Direction.UP))
				return Direction.DOWN;

			Direction incomingOpposite = incomingFace; // init as same so it doesn't break in the remove later
			if(incomingFace.getAxis() != Axis.Y) {
				incomingOpposite = incomingFace.getOpposite();
				if(pipe.canFit(stack, pipePos.offset(incomingOpposite), incomingFace))
					return incomingOpposite;
			}

			List<Direction> sides = new ArrayList<>(HORIZONTAL_SIDES_LIST);
			sides.remove(incomingFace);
			sides.remove(incomingOpposite);

			Random rng = new Random(rngSeed);
			rngSeed = rng.nextLong();
			Collections.shuffle(sides, rng);
			for(Direction side : sides) {
				if(pipe.canFit(stack, pipePos.offset(side), side.getOpposite()))
					return side;
			}

			if(incomingFace != Direction.UP && pipe.canFit(stack, pipePos.offset(Direction.UP), Direction.DOWN))
				return Direction.UP;

			return null;
		}

		public float getTimeFract(float partial) {
			return (ticksInPipe + partial) / PipesModule.pipeSpeed;
		}

		public void writeToNBT(CompoundNBT cmp) {
			stack.write(cmp);
			cmp.putInt(TAG_TICKS, ticksInPipe);
			cmp.putInt(TAG_INCOMING, incomingFace.ordinal());
			cmp.putInt(TAG_OUTGOING, outgoingFace.ordinal());
			cmp.putLong(TAG_RNG_SEED, rngSeed);
			cmp.putInt(TAG_TIME_IN_WORLD, timeInWorld);
		}

		public static PipeItem readFromNBT(CompoundNBT cmp) {
			ItemStack stack = ItemStack.read(cmp);
			Direction inFace = Direction.values()[cmp.getInt(TAG_INCOMING)];
			long rngSeed = cmp.getLong(TAG_RNG_SEED);
			PipeItem item = new PipeItem(stack, inFace, rngSeed);
			item.ticksInPipe = cmp.getInt(TAG_TICKS);
			item.outgoingFace = Direction.values()[cmp.getInt(TAG_OUTGOING)];
			item.timeInWorld = cmp.getInt(TAG_TIME_IN_WORLD);

			return item;
		}

	}

}
