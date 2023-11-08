package org.violetmoon.quark.content.building.module;

import com.google.common.collect.Lists;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolActions;

import java.util.List;
import java.util.function.BooleanSupplier;

import org.violetmoon.quark.base.block.QuarkBlock;
import org.violetmoon.quark.base.block.QuarkFlammableBlock;
import org.violetmoon.quark.base.block.QuarkFlammablePillarBlock;
import org.violetmoon.quark.base.config.Config;
import org.violetmoon.quark.base.handler.FuelHandler;
import org.violetmoon.quark.base.handler.ToolInteractionHandler;
import org.violetmoon.zeta.event.bus.LoadEvent;
import org.violetmoon.zeta.event.load.ZLoadComplete;
import org.violetmoon.zeta.event.load.ZRegister;
import org.violetmoon.zeta.module.ZetaLoadModule;
import org.violetmoon.zeta.module.ZetaModule;
import org.violetmoon.zeta.util.Hint;

@ZetaLoadModule(category = "building")
public class CompressedBlocksModule extends ZetaModule {

	@Config(name = "Charcoal Block and Blaze Lantern Stay On Fire Forever", flag = "compressed_blocks_burn_forever")
	public static boolean burnsForever = true;

	@Config(name = "Charcoal Block Fuel Time")
	@Config.Min(0)
	public static int charcoalBlockFuelTime = 16000;

	@Config(name = "Blaze Lantern Fuel Time")
	@Config.Min(0)
	public static int blazeLanternFuelTime = 24000;

	@Config(name = "Stick Block Fuel Time")
	@Config.Min(0)
	public static int stickBlockFuelTime = 900;
	
	@Config(flag = "charcoal_block") public static boolean enableCharcoalBlock = true;
	@Config(flag = "sugar_cane_block") public static boolean enableSugarCaneBlock = true;
	@Config(flag = "cactus_block") public static boolean enableCactusBlock = true;
	@Config(flag = "chorus_fruit_block") public static boolean enableChorusFruitBlock = true;
	@Config(flag = "stick_block") public static boolean enableStickBlock = true;

	@Config(flag = "apple_crate") public static boolean enableAppleCrate = true;
	@Config(flag = "golden_apple_crate") public static boolean enableGoldenAppleCrate = true;
	@Config(flag = "potato_crate") public static boolean enablePotatoCrate = true;
	@Config(flag = "carrot_crate") public static boolean enableCarrotCrate = true;
	@Config(flag = "golden_carrot_crate") public static boolean enableGoldenCarrotCrate = true;
	@Config(flag = "beetroot_crate") public static boolean enableBeetrootCrate = true;

	@Config(flag = "cocoa_beans_sack") public static boolean enableCocoaBeanSack = true;
	@Config(flag = "nether_wart_sack") public static boolean enableNetherWartSack = true;
	@Config(flag = "gunpowder_sack") public static boolean enableGunpowderSack = true;
	@Config(flag = "berry_sack") public static boolean enableBerrySack = true;
	@Config(flag = "glowberry_sack") public static boolean enableGlowBerrySack = true;

	@Config(flag = "blaze_lantern") public static boolean enableBlazeLantern = true;
	@Config(flag = "bonded_leather") public static boolean enableBondedLeather = true;
	@Config(flag = "bonded_rabbit_hide") public static boolean enableBondedRabbitHide = true;

	@Hint("compressed_blocks_burn_forever") public static Block charcoal_block;
	@Hint("compressed_blocks_burn_forever") public static Block blaze_lantern;
	@Hint("golden_apple_crate")	public static Block golden_apple_crate;
	
	public static Block stick_block;

	private final List<Block> compostable = Lists.newArrayList();

	@LoadEvent
	public final void register(ZRegister event) {
		charcoal_block = new QuarkBlock("charcoal_block", this, CreativeModeTab.TAB_BUILDING_BLOCKS,
				Block.Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
						.requiresCorrectToolForDrops()
						.strength(5F, 10F)
						.sound(SoundType.STONE))
				.setCondition(() -> enableCharcoalBlock);
		
		pillar("sugar_cane", MaterialColor.COLOR_LIGHT_GREEN, true, () -> enableSugarCaneBlock, 200);
		pillar("cactus", MaterialColor.COLOR_GREEN, true, () -> enableCactusBlock, 50);
		pillar("chorus_fruit", MaterialColor.COLOR_PURPLE, false, () -> enableChorusFruitBlock, 10);
		stick_block = pillar("stick", MaterialColor.WOOD, false, () -> enableStickBlock, 300);

		golden_apple_crate = crate("golden_apple", MaterialColor.GOLD, false, () -> enableGoldenAppleCrate);
		crate("apple", MaterialColor.COLOR_RED, true, () -> enableAppleCrate);
		crate("potato", MaterialColor.COLOR_ORANGE, true, () -> enablePotatoCrate);
		crate("carrot", MaterialColor.TERRACOTTA_ORANGE, true, () -> enableCarrotCrate);
		crate("golden_carrot", MaterialColor.GOLD, false, () -> enableGoldenCarrotCrate);
		crate("beetroot", MaterialColor.COLOR_RED, true, () -> enableBeetrootCrate);

		sack("cocoa_beans", MaterialColor.COLOR_BROWN, true, () -> enableCocoaBeanSack);
		sack("nether_wart", MaterialColor.COLOR_RED, true, () -> enableNetherWartSack);
		sack("gunpowder", MaterialColor.COLOR_GRAY, false, () -> enableGunpowderSack);
		sack("berry", MaterialColor.COLOR_RED, true, () -> enableBerrySack);
		sack("glowberry", MaterialColor.COLOR_YELLOW, 14, true, () -> enableGlowBerrySack);

		blaze_lantern = new QuarkBlock("blaze_lantern", this, CreativeModeTab.TAB_BUILDING_BLOCKS,
				Block.Properties.of(Material.GLASS, DyeColor.YELLOW)
				.strength(0.3F)
				.sound(SoundType.GLASS)
				.lightLevel(b -> 15))
		.setCondition(() -> enableBlazeLantern);
		
		new QuarkBlock("bonded_leather", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.of(Material.WOOL, DyeColor.ORANGE)
				.strength(0.4F)
				.sound(SoundType.WOOL))
		.setCondition(() -> enableBondedLeather);
		
		new QuarkBlock("bonded_rabbit_hide", this, CreativeModeTab.TAB_BUILDING_BLOCKS, Block.Properties.of(Material.WOOL, DyeColor.WHITE)
				.strength(0.4F)
				.sound(SoundType.WOOL))
		.setCondition(() -> enableBondedRabbitHide);
	}

	@LoadEvent
	public void loadComplete(ZLoadComplete event) {
		event.enqueueWork(() -> {
			for(Block block : compostable)
				if(block.asItem() != null)
					ComposterBlock.COMPOSTABLES.put(block.asItem(), 1F);
		});
			
		FuelHandler.addFuel(stick_block, stickBlockFuelTime);
		FuelHandler.addFuel(charcoal_block, charcoalBlockFuelTime);
		FuelHandler.addFuel(blaze_lantern, blazeLanternFuelTime);
	}

	private Block pillar(String name, MaterialColor color, boolean compost, BooleanSupplier cond, int flammability) {
		Block block = new QuarkFlammablePillarBlock(name + "_block", this, CreativeModeTab.TAB_BUILDING_BLOCKS, flammability,
				Block.Properties.of(Material.WOOD, color)
				.strength(0.5F)
				.sound(SoundType.WOOD))
		.setCondition(cond);

		if (compost)
			compostable.add(block);
		return block;
	}
	
	private Block crate(String name, MaterialColor color, boolean compost, BooleanSupplier cond) {
		Block block = new QuarkFlammableBlock(name + "_crate", this, CreativeModeTab.TAB_DECORATIONS, 150,
				Block.Properties.of(Material.WOOD, color)
				.strength(1.5F)
				.sound(SoundType.WOOD))
		.setCondition(cond);

		if (compost)
			compostable.add(block);
		return block;
	}

	private Block sack(String name, MaterialColor color, boolean compost, BooleanSupplier cond) {
		return sack(name, color, 0, compost, cond);
	}
	
	private Block sack(String name, MaterialColor color, int light, boolean compost, BooleanSupplier cond) {
		Block block = new QuarkFlammableBlock(name + "_sack", this, CreativeModeTab.TAB_DECORATIONS, 150,
				Block.Properties.of(Material.WOOL, color)
				.strength(0.5F)
				.lightLevel(s -> light)
				.sound(SoundType.WOOL))
		.setCondition(cond);

		if (compost)
			compostable.add(block);
		return block;
	}

}
