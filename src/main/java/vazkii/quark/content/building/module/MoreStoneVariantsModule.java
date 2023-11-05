package vazkii.quark.content.building.module;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.content.building.block.MyalitePillarBlock;
import vazkii.quark.content.world.block.MyaliteBlock;
import vazkii.quark.content.world.module.NewStoneTypesModule;
import vazkii.zeta.event.ZGatherAdditionalFlags;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;

import java.util.function.BooleanSupplier;

@ZetaLoadModule(category = "building")
public class MoreStoneVariantsModule extends ZetaModule {

	@Config(flag = "stone_bricks") public boolean enableBricks = true;
	@Config(flag = "stone_chiseled") public boolean enableChiseledBricks = true;
	@Config(flag = "stone_pillar") public boolean enablePillar = true;
	
	@LoadEvent
	public final void register(ZRegister event) {
		expandVanillaStone(this, Blocks.CALCITE, "calcite");
		expandVanillaStone(this, Blocks.DRIPSTONE_BLOCK, "dripstone");
		expandVanillaStone(this, Blocks.TUFF, "tuff");
		
		BooleanSupplier _true = () -> true;
		add("granite", MaterialColor.DIRT, SoundType.STONE, _true);
		add("diorite", MaterialColor.QUARTZ, SoundType.STONE, _true);
		add("andesite", MaterialColor.STONE, SoundType.STONE, _true);
		add("calcite", MaterialColor.TERRACOTTA_WHITE, SoundType.CALCITE, _true);
		add("dripstone", MaterialColor.TERRACOTTA_BROWN, SoundType.DRIPSTONE_BLOCK, _true);
		add("tuff", MaterialColor.TERRACOTTA_GRAY, SoundType.TUFF, _true);
		
		add("limestone", MaterialColor.STONE, SoundType.STONE, () -> NewStoneTypesModule.enableLimestone);
		add("jasper", MaterialColor.TERRACOTTA_RED, SoundType.STONE, () -> NewStoneTypesModule.enableJasper);
		add("shale", MaterialColor.ICE, SoundType.STONE, () -> NewStoneTypesModule.enableShale);
		
		add("myalite", MaterialColor.COLOR_PURPLE, SoundType.STONE, () -> NewStoneTypesModule.enableMyalite, MyaliteBlock::new, MyalitePillarBlock::new);
	}

	@PlayEvent
	public final void moreFlags(ZGatherAdditionalFlags event) {
		ConfigFlagManager manager = event.flagManager();
		manager.putFlag(this, "granite", true);
		manager.putFlag(this, "diorite", true);
		manager.putFlag(this, "andesite", true);
		manager.putFlag(this, "calcite", true);
		manager.putFlag(this, "dripstone", true);
		manager.putFlag(this, "tuff", true);
	}
	
	public static void expandVanillaStone(ZetaModule module, Block raw, String name) {
		NewStoneTypesModule.makeStone(module, raw, name, null, null, () -> true, null, QuarkBlock::new);
	}
	
	private void add(String name, MaterialColor color, SoundType sound, BooleanSupplier cond) {
		add(name, color, sound, cond, QuarkBlock::new, QuarkPillarBlock::new);
	}
	
	private void add(String name, MaterialColor color, SoundType sound, BooleanSupplier cond, QuarkBlock.Constructor<QuarkBlock> constr, QuarkBlock.Constructor<QuarkPillarBlock> pillarConstr) {
		Block.Properties props = Block.Properties.of(Material.STONE, color)
				.requiresCorrectToolForDrops()
				.sound(sound)
				.strength(1.5F, 6.0F);
		
		QuarkBlock bricks = constr.make(name + "_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props)
				.setCondition(() -> cond.getAsBoolean() && enableBricks);
		VariantHandler.addSlabStairsWall(bricks);
		
		constr.make("chiseled_" + name + "_bricks", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props)
				.setCondition(() -> cond.getAsBoolean() && enableBricks && enableChiseledBricks);
		pillarConstr.make(name + "_pillar", this, CreativeModeTab.TAB_BUILDING_BLOCKS, props)
				.setCondition(() -> cond.getAsBoolean() && enablePillar);
	}
	
}
