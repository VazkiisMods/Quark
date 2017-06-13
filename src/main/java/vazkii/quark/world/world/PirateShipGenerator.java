/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [02/07/2016, 23:38:15 (GMT)]
 */
package vazkii.quark.world.world;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;
import vazkii.quark.base.handler.DimensionConfig;
import vazkii.quark.world.entity.EntityPirate;
import vazkii.quark.world.feature.PirateShips;

public class PirateShipGenerator implements IWorldGenerator {

	DimensionConfig dims;
	
	public PirateShipGenerator(DimensionConfig dims) {
		this.dims = dims;
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		if(!(world instanceof WorldServer) || !dims.canSpawnHere(world))
			return;
		WorldServer sWorld = (WorldServer) world;

		int x = chunkX * 16 + random.nextInt(16);
		int z = chunkZ * 16 + random.nextInt(16);

		BlockPos xzPos = new BlockPos(x, 1, z);
		Biome biome = world.getBiomeForCoordsBody(xzPos);
		if(biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN) {
			if(random.nextInt(PirateShips.rarity) == 0) {
				BlockPos pos = getTopLiquidBlock(world, new BlockPos(x, 0, z));
				IBlockState state = world.getBlockState(pos.down());
				if(state.getBlock() != Blocks.WATER)
					return;
				
				pos = new BlockPos(pos.getX(), pos.getY() - 3, pos.getZ());
				generateShipAt(sWorld, random, pos);
			}
		}
	}

	public static void generateShipAt(WorldServer world, Random random, BlockPos pos) {
		MinecraftServer server = world.getMinecraftServer();
		Template template = world.getStructureTemplateManager().getTemplate(server, PirateShips.SHIP_STRUCTURE);
		PlacementSettings settings = new PlacementSettings();
		settings.setRotation(Rotation.values()[random.nextInt(Rotation.values().length)]);
		
		BlockPos size = template.getSize();
		for(int x = 0; x < size.getX(); x++)
			for(int y = 0; y < size.getY(); y++)
				for(int z = 0; z < size.getZ(); z++) {
					BlockPos checkPos = pos.add(template.transformedBlockPos(settings, new BlockPos(x, y, z)));
					IBlockState checkState = world.getBlockState(checkPos);
					if(!checkState.getBlock().isAir(checkState, world, checkPos) && checkState.getBlock() != Blocks.WATER)
						return; // Obstructed, can't generate here
				}
		
		template.addBlocksToWorld(world, pos, settings);

		Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, settings);
		for(Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			String[] tokens = entry.getValue().split(" ");
			if(tokens.length == 0)
				return;

			BlockPos dataPos = entry.getKey();
			EntityPirate pirate;

			switch(tokens[0]) {
			case "captain_pirate":
				pirate = new EntityPirate(world);
				pirate.setPosition(dataPos.getX() + 0.5, dataPos.getY(), dataPos.getZ() + 0.5);
				pirate.setItemStackToSlot(EntityEquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
				pirate.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
				pirate.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
				world.spawnEntity(pirate);
				break;
			case "bow_pirate":
				pirate = new EntityPirate(world);
				pirate.setPosition(dataPos.getX() + 0.5, dataPos.getY(), dataPos.getZ() + 0.5);
				pirate.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
				pirate.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
				world.spawnEntity(pirate);
				break;
			case "sword_pirate":
				pirate = new EntityPirate(world);
				pirate.setPosition(dataPos.getX() + 0.5, dataPos.getY(), dataPos.getZ() + 0.5);
				pirate.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
				pirate.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(PirateShips.pirate_hat));
				world.spawnEntity(pirate);
				break;
			case "booty":
				float chance = tokens.length == 3 ? 1F : 0.75F;

				if(random.nextFloat() > chance)
					break;

				String chestOrientation = tokens[1];
				EnumFacing chestFacing = settings.getRotation().rotate(EnumFacing.byName(chestOrientation));
				IBlockState chestState = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, chestFacing);
				world.setBlockState(dataPos, chestState);

				TileEntity tile = world.getTileEntity(dataPos);
				if(tile != null && tile instanceof TileEntityLockableLoot)
					((TileEntityLockableLoot) tile).setLootTable(PirateShips.PIRATE_CHEST_LOOT_TABLE, random.nextLong());
				break;
			case "cannon":
				String dispenserOrientation = tokens[1];
				EnumFacing dispenserFacing = settings.getRotation().rotate(EnumFacing.byName(dispenserOrientation));
				IBlockState dispenserState = Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, dispenserFacing);
				world.setBlockState(dataPos, dispenserState);

				TileEntityDispenser dispenser = (TileEntityDispenser) world.getTileEntity(dataPos);
				if(dispenser != null)
					dispenser.setInventorySlotContents(random.nextInt(9), new ItemStack(Items.FIRE_CHARGE, 5 + random.nextInt(20)));
				break;
			}
		}
	}

	private static BlockPos getTopLiquidBlock(World world, BlockPos pos) {
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		BlockPos blockpos;
		BlockPos blockpos1;

		for(blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16, pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1) {
			blockpos1 = blockpos.down();
			IBlockState state = chunk.getBlockState(blockpos1);

			if(state.getBlock() instanceof BlockLiquid)
				break;
		}

		return blockpos;
	}

}
