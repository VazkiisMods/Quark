package vazkii.quark.world.world.underground;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.base.module.ModuleLoader;

public class UndergroundBiomeSandstone extends BasicUndergroundBiome {

	public static final ResourceLocation HUSK_GRAVE_STRUCTURE = new ResourceLocation("quark", "husk_grave");
	
	int stalactiteChance, chiseledSandstoneChance, deadBushChance;
	boolean enableSand, allowGenInMesa;
	
	public UndergroundBiomeSandstone() {
		super(Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState());
	}
	
	@Override
	public boolean apply(World world, BlockPos center, int radiusX, int radiusY, int radiusZ) {
		if(!allowGenInMesa && BiomeDictionary.hasType(world.getBiome(center), BiomeDictionary.Type.MESA))
			return false;
				
		return super.apply(world, center, radiusX, radiusY, radiusZ);
	}
	
	@Override
	public void fillCeiling(World world, BlockPos pos, IBlockState state) {
		if(stalactiteChance > 0 && world.rand.nextInt(stalactiteChance) == 0)
			world.setBlockState(pos.down(), ceilingState, 2);
		
		super.fillCeiling(world, pos, state);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		if(enableSand && world.rand.nextBoolean()) {
			world.setBlockState(pos, Blocks.SAND.getDefaultState(), 2);
			if(deadBushChance > 0 && world.rand.nextInt(deadBushChance) == 0)
				world.setBlockState(pos.up(), Blocks.DEADBUSH.getDefaultState(), 2);
		} else super.fillFloor(world, pos, state);
	}
	
	@Override
	public void fillWall(World world, BlockPos pos, IBlockState state) {
		if(chiseledSandstoneChance > 0 && world.rand.nextInt(chiseledSandstoneChance) == 0)
			world.setBlockState(pos, wallState.withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED), 2);
		else super.fillWall(world, pos, state);
	}
	
	@Override
	public boolean hasDungeon() {
		return true;
	}
	
	@Override
	public int getDungeonDistance() {
		return 35;
	}
	
	@Override
	public void spawnDungeon(WorldServer world, BlockPos pos, EnumFacing side) {
		if(side == null)
			side = EnumFacing.NORTH;
		
		switch(side) {
		case NORTH:
			pos = pos.add(3, -7, 6);
			break;
		case SOUTH:
			pos = pos.add(-3, -7, -6);
			break;
		case EAST:
			pos = pos.add(-6, -7, 3);
			break;
		case WEST:
			pos = pos.add(6, -7, -3);
			break;
		default: break; 
		}
		
		MinecraftServer server = world.getMinecraftServer();
		Template template = world.getStructureTemplateManager().getTemplate(server, HUSK_GRAVE_STRUCTURE);
		PlacementSettings settings = new PlacementSettings();
		settings.setRotation(rotationFromFacing(side.getOpposite()));
		
		template.addBlocksToWorld(world, pos, settings);

		Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, settings);
		for(Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			BlockPos dataPos = entry.getKey();
			switch(entry.getValue()) {
			case "spawner":
				world.setBlockState(dataPos, Blocks.MOB_SPAWNER.getDefaultState(), 2);
	            TileEntity spawner = world.getTileEntity(dataPos);

	            if(spawner instanceof TileEntityMobSpawner)
	                ((TileEntityMobSpawner) spawner).getSpawnerBaseLogic().setEntityId(EntityList.getKey(EntityZombie.class));
	            break;
			case "chest":
				IBlockState chestState = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
				world.setBlockState(dataPos, chestState);

				TileEntity chest = world.getTileEntity(dataPos);
				if(chest != null && chest instanceof TileEntityLockableLoot)
					((TileEntityLockableLoot) chest).setLootTable(LootTableList.CHESTS_DESERT_PYRAMID, world.rand.nextLong());
				break;
			}
		}
	}
	
	@Override
	public void setupConfig(String category) {
		stalactiteChance = ModuleLoader.config.getInt("Stalactite Chance", category, 10, 0, Integer.MAX_VALUE, "The higher, the less stalactites will spawn");
		chiseledSandstoneChance = ModuleLoader.config.getInt("Chiseled Sandstone Chance", category, 10, 0, Integer.MAX_VALUE, "The higher, the less chiseled sandstone will spawn");
		deadBushChance = ModuleLoader.config.getInt("Dead Bush Chance", category, 20, 0, Integer.MAX_VALUE, "The higher, the less dead bushes will spawn");
		enableSand = ModuleLoader.config.getBoolean("Enable Sand Floors", category, true, "");
		allowGenInMesa = ModuleLoader.config.getBoolean("Allow in Mesa biomes", category, false, "");
	}
	
}
