package vazkii.quark.oddities.tile;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.oddities.magnetsystem.IMagnetMoveAction;
import vazkii.quark.oddities.magnetsystem.MagnetSystem;
import vazkii.quark.oddities.module.MagnetsModule;

public class MagnetizedBlockTileEntity extends TileEntity implements ITickableTileEntity {
    private BlockState magnetState;
    private CompoundNBT subTile;
    private Direction magnetFacing;
    private static final ThreadLocal<Direction> MOVING_ENTITY = ThreadLocal.withInitial(() -> null);
    private float progress;
    private float lastProgress;
    private long lastTicked;

    public MagnetizedBlockTileEntity() {
        super(MagnetsModule.magnetizedBlockType);
    }

    public MagnetizedBlockTileEntity(BlockState magnetStateIn, CompoundNBT subTileIn, Direction magnetFacingIn) {
        this();
        this.magnetState = magnetStateIn;
        this.subTile = subTileIn;
        this.magnetFacing = magnetFacingIn;
    }

    public Direction getFacing() {
        return this.magnetFacing;
    }

    public float getProgress(float ticks) {
        if (ticks > 1.0F) {
            ticks = 1.0F;
        }

        return MathHelper.lerp(ticks, this.lastProgress, this.progress);
    }

    @OnlyIn(Dist.CLIENT)
    public float getOffsetX(float ticks) {
        return this.magnetFacing.getXOffset() * this.getExtendedProgress(this.getProgress(ticks));
    }

    @OnlyIn(Dist.CLIENT)
    public float getOffsetY(float ticks) {
        return this.magnetFacing.getYOffset() * this.getExtendedProgress(this.getProgress(ticks));
    }

    @OnlyIn(Dist.CLIENT)
    public float getOffsetZ(float ticks) {
        return this.magnetFacing.getZOffset() * this.getExtendedProgress(this.getProgress(ticks));
    }

    private float getExtendedProgress(float partialTicks) {
        return partialTicks - 1.0F;
    }

    private void moveCollidedEntities(float progress) {
        if (this.world == null)
            return;

        Direction direction = this.magnetFacing;
        double movement = (progress - this.progress);
        VoxelShape collision = magnetState.getCollisionShape(this.world, this.getPos());
        if (!collision.isEmpty()) {
            List<AxisAlignedBB> boundingBoxes = collision.toBoundingBoxList();
            AxisAlignedBB containingBox = this.moveByPositionAndProgress(this.getEnclosingBox(boundingBoxes));
            List<Entity> entities = this.world.getEntitiesWithinAABBExcludingEntity(null, this.getMovementArea(containingBox, direction, movement).union(containingBox));
            if (!entities.isEmpty()) {
                boolean sticky = this.magnetState.getBlock().isStickyBlock(this.magnetState);

                for (Entity entity : entities) {
                    if (entity.getPushReaction() != PushReaction.IGNORE) {
                        if (sticky) {
                            Vec3d motion = entity.getMotion();
                            double dX = motion.x;
                            double dY = motion.y;
                            double dZ = motion.z;
                            switch (direction.getAxis()) {
                                case X:
                                    dX = direction.getXOffset();
                                    break;
                                case Y:
                                    dY = direction.getYOffset();
                                    break;
                                case Z:
                                    dZ = direction.getZOffset();
                            }

                            entity.setMotion(dX, dY, dZ);
                        }

                        double motion = 0.0D;

                        for (AxisAlignedBB aList : boundingBoxes) {
                            AxisAlignedBB movementArea = this.getMovementArea(this.moveByPositionAndProgress(aList), direction, movement);
                            AxisAlignedBB entityBox = entity.getBoundingBox();
                            if (movementArea.intersects(entityBox)) {
                                motion = Math.max(motion, this.getMovement(movementArea, direction, entityBox));
                                if (motion >= movement) {
                                    break;
                                }
                            }
                        }

                        if (motion > 0) {
                            motion = Math.min(motion, movement) + 0.01D;
                            MOVING_ENTITY.set(direction);
                            entity.move(MoverType.PISTON, new Vec3d(motion * direction.getXOffset(), motion * direction.getYOffset(), motion * direction.getZOffset()));
                            MOVING_ENTITY.set(null);
                        }
                    }
                }

            }
        }
    }

    private AxisAlignedBB getEnclosingBox(List<AxisAlignedBB> boxes) {
        double minX = 0.0D;
        double minY = 0.0D;
        double minZ = 0.0D;
        double maxX = 1.0D;
        double maxY = 1.0D;
        double maxZ = 1.0D;

        for(AxisAlignedBB bb : boxes) {
            minX = Math.min(bb.minX, minX);
            minY = Math.min(bb.minY, minY);
            minZ = Math.min(bb.minZ, minZ);
            maxX = Math.max(bb.maxX, maxX);
            maxY = Math.max(bb.maxY, maxY);
            maxZ = Math.max(bb.maxZ, maxZ);
        }

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private double getMovement(AxisAlignedBB bb1, Direction facing, AxisAlignedBB bb2) {
        switch(facing.getAxis()) {
            case X:
                return getDeltaX(bb1, facing, bb2);
            case Z:
                return getDeltaZ(bb1, facing, bb2);
            default:
                return getDeltaY(bb1, facing, bb2);
        }
    }

    private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB bb) {
        double progress = this.getExtendedProgress(this.progress);
        return bb.offset(this.pos.getX() + progress * this.magnetFacing.getXOffset(), this.pos.getY() + progress * this.magnetFacing.getYOffset(), this.pos.getZ() + progress * this.magnetFacing.getZOffset());
    }

    private AxisAlignedBB getMovementArea(AxisAlignedBB bb, Direction dir, double movement) {
        double d0 = movement * dir.getAxisDirection().getOffset();
        double d1 = Math.min(d0, 0.0D);
        double d2 = Math.max(d0, 0.0D);
        switch(dir) {
            case WEST:
                return new AxisAlignedBB(bb.minX + d1, bb.minY, bb.minZ, bb.minX + d2, bb.maxY, bb.maxZ);
            case EAST:
                return new AxisAlignedBB(bb.maxX + d1, bb.minY, bb.minZ, bb.maxX + d2, bb.maxY, bb.maxZ);
            case DOWN:
                return new AxisAlignedBB(bb.minX, bb.minY + d1, bb.minZ, bb.maxX, bb.minY + d2, bb.maxZ);
            case NORTH:
                return new AxisAlignedBB(bb.minX, bb.minY, bb.minZ + d1, bb.maxX, bb.maxY, bb.minZ + d2);
            case SOUTH:
                return new AxisAlignedBB(bb.minX, bb.minY, bb.maxZ + d1, bb.maxX, bb.maxY, bb.maxZ + d2);
            default:
                return new AxisAlignedBB(bb.minX, bb.maxY + d1, bb.minZ, bb.maxX, bb.maxY + d2, bb.maxZ);
        }
    }

    private static double getDeltaX(AxisAlignedBB bb1, Direction facing, AxisAlignedBB bb2) {
        return facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? bb1.maxX - bb2.minX : bb2.maxX - bb1.minX;
    }

    private static double getDeltaY(AxisAlignedBB bb1, Direction facing, AxisAlignedBB bb2) {
        return facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? bb1.maxY - bb2.minY : bb2.maxY - bb1.minY;
    }

    private static double getDeltaZ(AxisAlignedBB bb1, Direction facing, AxisAlignedBB bb2) {
        return facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? bb1.maxZ - bb2.minZ : bb2.maxZ - bb1.minZ;
    }

    public BlockState getMagnetState() {
        return this.magnetState;
    }


    private IMagnetMoveAction getMoveAction() {
        Block block = magnetState.getBlock();
        if(block instanceof IMagnetMoveAction)
            return (IMagnetMoveAction) block;

        return MagnetSystem.getMoveAction(block);
    }

    public void finalizeContents(BlockState blockState) {
        if (world == null || world.isRemote)
            return;

        SoundType soundType = blockState.getSoundType();
        world.playSound(null, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1) * 0.05F, soundType.getPitch() * 0.8F);

        TileEntity newTile = getSubTile();
        if (newTile != null)
            world.setTileEntity(pos, newTile);

        IMagnetMoveAction action = getMoveAction();
        if(action != null)
            action.onMoved(world, pos, magnetFacing, blockState, newTile);
    }
    
    public TileEntity getSubTile() {
        if (subTile != null && !subTile.isEmpty()) {
            CompoundNBT tileData = subTile.copy();
            tileData.putInt("x", this.pos.getX());
            tileData.putInt("y", this.pos.getY());
            tileData.putInt("z", this.pos.getZ());
            return TileEntity.create(subTile);
        }
        
        return null;
    }

    public void clearMagnetTileEntity() {
        if (this.lastProgress < 1.0F && this.world != null) {
            this.progress = 1.0F;
            this.lastProgress = this.progress;

            this.world.removeTileEntity(this.pos);
            this.remove();
            if (this.world.getBlockState(this.pos).getBlock() == MagnetsModule.magnetized_block) {
                BlockState blockstate = Block.getValidBlockForPosition(this.magnetState, this.world, this.pos);

                this.world.setBlockState(this.pos, blockstate, 3);
                this.world.neighborChanged(this.pos, blockstate.getBlock(), this.pos);

                finalizeContents(blockstate);
            }
        }

    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick() {
        if (this.world == null)
            return;
        this.lastTicked = this.world.getGameTime();
        this.lastProgress = this.progress;
        if (this.lastProgress >= 1.0F) {
            this.world.removeTileEntity(this.pos);
            this.remove();
            if (this.magnetState != null && this.world.getBlockState(this.pos).getBlock() == MagnetsModule.magnetized_block) {
                BlockState blockstate = Block.getValidBlockForPosition(this.magnetState, this.world, this.pos);
                if (blockstate.isAir()) {
                    this.world.setBlockState(this.pos, this.magnetState, 84);
                    Block.replaceBlock(this.magnetState, blockstate, this.world, this.pos, 3);
                } else {
                    if (blockstate.has(BlockStateProperties.WATERLOGGED) && blockstate.get(BlockStateProperties.WATERLOGGED)) {
                        blockstate = blockstate.with(BlockStateProperties.WATERLOGGED, Boolean.FALSE);
                    }

                    this.world.setBlockState(this.pos, blockstate, 67);
                    this.world.neighborChanged(this.pos, blockstate.getBlock(), this.pos);

                    finalizeContents(blockstate);
                }
            }

        } else {
            float newProgress = this.progress + 0.5F;
            this.moveCollidedEntities(newProgress);
            this.progress = newProgress;
            if (this.progress >= 1.0F) {
                this.progress = 1.0F;
            }

        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.magnetState = NBTUtil.readBlockState(compound.getCompound("blockState"));
        this.magnetFacing = Direction.byIndex(compound.getInt("facing"));
        this.progress = compound.getFloat("progress");
        this.lastProgress = this.progress;
        this.subTile = compound.getCompound("subTile");
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return writeNBTData(super.write(new CompoundNBT()), false);
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return writeNBTData(super.write(new CompoundNBT()), true);
    }

    private CompoundNBT writeNBTData(CompoundNBT compound, boolean includeSubTile) {
        compound.put("blockState", NBTUtil.writeBlockState(this.magnetState));
        if (includeSubTile)
            compound.put("subTile", subTile);
        compound.putInt("facing", this.magnetFacing.getIndex());
        compound.putFloat("progress", this.lastProgress);
        return compound;
    }

    public VoxelShape getCollisionShape(IBlockReader world, BlockPos pos) {
        Direction direction = MOVING_ENTITY.get();
        if (this.progress < 1.0D && direction == this.magnetFacing) {
            return VoxelShapes.empty();
        } else {

            float progress = this.getExtendedProgress(this.progress);
            double dX = this.magnetFacing.getXOffset() * progress;
            double dY = this.magnetFacing.getYOffset() * progress;
            double dZ = this.magnetFacing.getZOffset() * progress;
            return magnetState.getCollisionShape(world, pos).withOffset(dX, dY, dZ);
        }
    }

    public long getLastTicked() {
        return this.lastTicked;
    }
}
