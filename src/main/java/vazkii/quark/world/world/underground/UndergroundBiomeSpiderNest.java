package vazkii.quark.world.world.underground;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.base.module.ModuleLoader;

public class UndergroundBiomeSpiderNest extends BasicUndergroundBiome {

	int floorCobwebChance, ceilingCobwebChance, caveSpiderSpawnerChance, nestCobwebChance, nestCobwebRange;
	
	public UndergroundBiomeSpiderNest() {
		super(Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState());
	}

	@Override
	public void fillCeiling(World world, BlockPos pos, IBlockState state) {
		super.fillCeiling(world, pos, state);
		placeCobweb(world, pos, EnumFacing.DOWN, ceilingCobwebChance);
	}
	
	@Override
	public void fillFloor(World world, BlockPos pos, IBlockState state) {
		super.fillFloor(world, pos, state);
		placeCobweb(world, pos, EnumFacing.UP, floorCobwebChance);
	}
	
	private void placeCobweb(World world, BlockPos pos, EnumFacing off, int chance) {
		if(chance > 0 && world.rand.nextInt(chance) == 0) {
			BlockPos placePos = off == null ? pos : pos.offset(off);
			world.setBlockState(placePos, Blocks.WEB.getDefaultState());
		}
	}
	
	@Override
	public boolean hasDungeon() {
		return true;
	}
	
	@Override
	public int getDungeonDistance() {
		return 12;
	}
	
	@Override
	public int[] getDefaultDungeonSettings() {
		return new int[] { 2, 3, 1 };
	}
	
	@Override
	public void spawnDungeon(WorldServer world, BlockPos pos, EnumFacing face) {
		BlockPos spawnerPos = pos.offset(face);
		world.setBlockState(spawnerPos, Blocks.MOB_SPAWNER.getDefaultState());
		
		Class<? extends Entity> e = EntitySpider.class;
		if(caveSpiderSpawnerChance > 0 && world.rand.nextInt(caveSpiderSpawnerChance) == 0)
			e = EntityCaveSpider.class;
		((TileEntityMobSpawner) world.getTileEntity(spawnerPos)).getSpawnerBaseLogic().setEntityId(EntityList.getKey(e));
		
		int range = 3;
		for(int x = -range; x < range + 1; x++)
			for(int y = -range; y < range + 1; y++)
				for(int z = -range; z < range + 1; z++) {
					BlockPos cobwebPos = spawnerPos.add(x, y, z);
					IBlockState stateAt = world.getBlockState(cobwebPos);
					if(stateAt.getBlock().isAir(stateAt, world, cobwebPos) || stateAt.getBlock().isReplaceable(world, cobwebPos))
						placeCobweb(world, cobwebPos, null, nestCobwebChance);
				}
	}
	
	@Override
	public void setupConfig(String category) {
		floorCobwebChance = ModuleLoader.config.getInt("Floor Cobweb Chance", category, 30, 0, Integer.MAX_VALUE, "The higher, the less floor cobwebs will spawn");
		ceilingCobwebChance = ModuleLoader.config.getInt("Ceiling Cobweb Chance", category, 10, 0, Integer.MAX_VALUE, "The higher, the less ceiling cobwebs will spawn");
		caveSpiderSpawnerChance = ModuleLoader.config.getInt("Cave Spider Spawner Chance", category, 4, 0, Integer.MAX_VALUE, "The (1 in X) chance for a spider spawner to be a cave spider spawner instead");
		nestCobwebChance = ModuleLoader.config.getInt("Nest Cobweb Chance", category, 2, 0, Integer.MAX_VALUE, "The higher, the less cobwebs will spawn in nests");
		nestCobwebRange = ModuleLoader.config.getInt("Nest Cobweb Range", category, 3, 0, Integer.MAX_VALUE, "The range for cobwebs to be spawned in spider nests");
	}

}

