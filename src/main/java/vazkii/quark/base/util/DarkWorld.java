package vazkii.quark.base.util;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.WorldInfo;

// TODO WIRE: I have no idea what you did with this but it don work

public class DarkWorld implements IWorld {
	
    private final IWorld parent;
    private final AbstractChunkProvider provider;
    private final WorldLightManager light;

    public DarkWorld(IWorld parent) {
        this.parent = parent;
        provider = new DarkChunkProvider(parent.getChunkProvider());
        light = new DarkLightManager(parent.getChunkProvider());
    }

    
	@Override
	public BiomeManager getBiomeManager() {
		return parent.getBiomeManager();
	}
	
	@Override
	public Biome getNoiseBiomeRaw(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
		return parent.getNoiseBiomeRaw(p_225604_1_, p_225604_2_, p_225604_3_);
	}
	
	@Override
	public WorldLightManager getLightManager() {
		return light;
	}

	@Override // breakBlock
	public boolean func_225521_a_(BlockPos arg0, boolean arg1, Entity arg2) {
		return false;
	}
    
    @Override
    public long getSeed() {
        return parent.getSeed();
    }

    @Nonnull
    @Override
    public ITickList<Block> getPendingBlockTicks() {
        return parent.getPendingBlockTicks();
    }

    @Nonnull
    @Override
    public ITickList<Fluid> getPendingFluidTicks() {
        return parent.getPendingFluidTicks();
    }

    @Nonnull
    @Override
    public World getWorld() {
        return parent.getWorld();
    }

    @Nonnull
    @Override
    public WorldInfo getWorldInfo() {
        return parent.getWorldInfo();
    }

    @Nonnull
    @Override
    public DifficultyInstance getDifficultyForLocation(@Nonnull BlockPos pos) {
        return parent.getDifficultyForLocation(pos);
    }

    @Nonnull
    @Override
    public AbstractChunkProvider getChunkProvider() {
        return provider;
    }

    @Nonnull
    @Override
    public Random getRandom() {
        return parent.getRandom();
    }

    @Override
    public void notifyNeighbors(@Nonnull BlockPos pos, @Nonnull Block blockIn) {
        parent.notifyNeighbors(pos, blockIn);
    }

    @Nonnull
    @Override
    public BlockPos getSpawnPoint() {
        return parent.getSpawnPoint();
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, @Nonnull BlockPos pos, @Nonnull SoundEvent soundIn, @Nonnull SoundCategory category, float volume, float pitch) {
        parent.playSound(player, pos, soundIn, category, volume, pitch);
    }

    @Override
    public void addParticle(@Nonnull IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        parent.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void playEvent(@Nullable PlayerEntity player, int type, @Nonnull BlockPos pos, int data) {
        parent.playEvent(player, type, pos, data);
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(@Nonnull BlockPos pos) {
        return parent.getTileEntity(pos);
    }

    @Nonnull
    @Override
    public BlockState getBlockState(@Nonnull BlockPos pos) {
        return parent.getBlockState(pos);
    }

    @Nonnull
    @Override
    public IFluidState getFluidState(@Nonnull BlockPos pos) {
        return parent.getFluidState(pos);
    }

    @Nonnull
    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, @Nonnull AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
        return parent.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Nonnull
    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(@Nonnull Class<? extends T> clazz, @Nonnull AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
        return parent.getEntitiesWithinAABB(clazz, aabb, filter);
    }

    @Nonnull
    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return parent.getPlayers();
    }

    @Nullable
    @Override
    public IChunk getChunk(int x, int z, @Nonnull ChunkStatus requiredStatus, boolean nonnull) {
        return parent.getChunk(x, z, requiredStatus, nonnull);
    }

    @Nonnull
    @Override
    public BlockPos getHeight(@Nonnull Heightmap.Type heightmapType, @Nonnull BlockPos pos) {
        return parent.getHeight(heightmapType, pos);
    }

    @Override
    public int getHeight(@Nonnull Heightmap.Type heightmapType, int x, int z) {
        return parent.getHeight(heightmapType, x, z);
    }

    @Override
    public int getSkylightSubtracted() {
        return 0;
    }

    @Nonnull
    @Override
    public WorldBorder getWorldBorder() {
        return parent.getWorldBorder();
    }

    @Override
    public boolean isRemote() {
        return parent.isRemote();
    }

    @Override
    public int getSeaLevel() {
        return parent.getSeaLevel();
    }

    @Nonnull
    @Override
    public Dimension getDimension() {
        return parent.getDimension();
    }

    @Nonnull
    @Override
    public Biome getBiome(@Nonnull BlockPos pos) {
        return parent.getBiome(pos);
    }

    @Override
    public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull BlockState newState, int flags) {
        return parent.setBlockState(pos, newState, flags);
    }

    @Override
    public boolean removeBlock(@Nonnull BlockPos pos, boolean isMoving) {
        return parent.removeBlock(pos, isMoving);
    }

    @Override
    public boolean destroyBlock(@Nonnull BlockPos pos, boolean dropBlock) {
        return parent.destroyBlock(pos, dropBlock);
    }

    @Override
    public boolean hasBlockState(@Nonnull BlockPos pos, @Nonnull Predicate<BlockState> predicate) {
        return parent.hasBlockState(pos, predicate);
    }

    private static class DarkLightManager extends WorldLightManager {

		public DarkLightManager(AbstractChunkProvider parent) {
			super(new DarkChunkProvider(parent), false, false);
		}
		
		@Override
		public int getLightSubtracted(BlockPos p_227470_1_, int p_227470_2_) {
			return 0;
		}
    	
    }

}
