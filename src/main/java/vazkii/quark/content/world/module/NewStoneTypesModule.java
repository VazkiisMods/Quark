package vazkii.quark.content.world.module;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.function.BooleanSupplier;

import com.google.common.collect.Maps;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkBlockWrapper;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.quark.base.world.generator.OreGenerator;
import vazkii.quark.content.world.block.MyaliteBlock;
import vazkii.quark.content.world.config.BigStoneClusterConfig;
import vazkii.quark.content.world.config.StoneTypeConfig;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZConfigChanged;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;

@LoadModule(category = "world", hasSubscriptions = true)
public class NewStoneTypesModule extends QuarkModule {

	@Config(flag = "limestone") public static boolean enableLimestone = true;
	@Config(flag = "jasper") public static boolean enableJasper = true;
	@Config(flag = "shale") public static boolean enableShale = true;
	@Config(flag = "myalite") public static boolean enableMyalite = true;

	public static boolean enabledWithLimestone, enabledWithJasper, enabledWithShale, enabledWithMyalite;

	@Config public static StoneTypeConfig limestone = new StoneTypeConfig();
	@Config public static StoneTypeConfig jasper = new StoneTypeConfig();
	@Config public static StoneTypeConfig shale = new StoneTypeConfig();
	@Config public static StoneTypeConfig myalite = new StoneTypeConfig(DimensionConfig.end(false));

	@Hint("limestone") public static Block limestoneBlock;
	@Hint("jasper") public static Block jasperBlock;
	@Hint("shale") public static Block shaleBlock;
	@Hint("myalite") public static Block myaliteBlock;

	public static Map<Block, Block> polishedBlocks = Maps.newHashMap();

	private static Queue<Runnable> defers = new ArrayDeque<>();

	@LoadEvent
	public final void register(ZRegister event) {
		limestoneBlock = makeStone(this, "limestone", limestone, BigStoneClustersModule.limestone, () -> enableLimestone, MaterialColor.STONE);
		jasperBlock = makeStone(this, "jasper", jasper, BigStoneClustersModule.jasper, () -> enableJasper, MaterialColor.TERRACOTTA_RED);
		shaleBlock = makeStone(this, "shale", shale, BigStoneClustersModule.shale, () -> enableShale, MaterialColor.ICE);
		myaliteBlock = makeStone(this, null, "myalite", myalite, BigStoneClustersModule.myalite, () -> enableMyalite, MaterialColor.COLOR_PURPLE, MyaliteBlock::new);
	}

	public static Block makeStone(QuarkModule module, String name, StoneTypeConfig config, BigStoneClusterConfig bigConfig, BooleanSupplier enabledCond, MaterialColor color) {
		return makeStone(module, null, name, config, bigConfig, enabledCond, color, QuarkBlock::new);
	}

	public static Block makeStone(QuarkModule module, final Block raw, String name, StoneTypeConfig config, BigStoneClusterConfig bigConfig, BooleanSupplier enabledCond, MaterialColor color, QuarkBlock.Constructor<QuarkBlock> constr) {
		BooleanSupplier trueEnabledCond = () -> (bigConfig == null || !bigConfig.enabled || !ModuleLoader.INSTANCE.isModuleEnabled(BigStoneClustersModule.class)) && enabledCond.getAsBoolean();

		Block.Properties props;
		if(raw != null)
			props = Block.Properties.copy(raw);
		else
			props = Block.Properties.of(Material.STONE, color)
				.requiresCorrectToolForDrops()
				.strength(1.5F, 6.0F);

		Block normal;
		if(raw != null)
			normal = raw;
		else
			normal = constr.make(name, module, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(enabledCond);

		QuarkBlock polished = constr.make("polished_" + name, module, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(enabledCond);
		polishedBlocks.put(normal, polished);

		VariantHandler.addSlabStairsWall(normal instanceof IQuarkBlock quarkBlock ? quarkBlock : new QuarkBlockWrapper(normal, module).setCondition(enabledCond));
		VariantHandler.addSlabAndStairs(polished);

		if(raw == null) {
			defers.add(() -> {
				WorldGenHandler.addGenerator(module, new OreGenerator(config.dimensions, config.oregenLower, normal.defaultBlockState(), OreGenerator.ALL_DIMS_STONE_MATCHER, trueEnabledCond), Decoration.UNDERGROUND_ORES, WorldGenWeights.NEW_STONES);
				WorldGenHandler.addGenerator(module, new OreGenerator(config.dimensions, config.oregenUpper, normal.defaultBlockState(), OreGenerator.ALL_DIMS_STONE_MATCHER, trueEnabledCond), Decoration.UNDERGROUND_ORES, WorldGenWeights.NEW_STONES);
			});
		}

		return normal;
	}

	@LoadEvent
	public final void configChanged(ZConfigChanged event) {
		enabledWithLimestone = enableLimestone && this.enabled;
		enabledWithJasper = enableJasper && this.enabled;
		enabledWithShale = enableShale && this.enabled;
		enabledWithMyalite = enableMyalite && this.enabled;
	}

	@LoadEvent
	public final void setup(ZCommonSetup event) {
		while(!defers.isEmpty())
			defers.poll().run();
	}

}
