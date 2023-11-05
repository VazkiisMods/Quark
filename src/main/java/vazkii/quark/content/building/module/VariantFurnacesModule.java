package vazkii.quark.content.building.module;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vazkii.quark.base.Quark;
import vazkii.quark.content.building.block.SoulFurnaceBlock;
import vazkii.quark.content.building.block.VariantFurnaceBlock;
import vazkii.quark.content.building.block.be.VariantFurnaceBlockEntity;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

import java.util.function.ToIntFunction;

@ZetaLoadModule(category = "building")
public class VariantFurnacesModule extends ZetaModule {

	public static BlockEntityType<VariantFurnaceBlockEntity> blockEntityType;

	public static Block deepslateFurnace;
	@Hint public static Block blackstoneFurnace;

	@LoadEvent
	public final void register(ZRegister event) {
		deepslateFurnace = new VariantFurnaceBlock("deepslate", this, Properties.copy(Blocks.DEEPSLATE).lightLevel(litBlockEmission(13)));
		blackstoneFurnace = new SoulFurnaceBlock("blackstone", this, Properties.copy(Blocks.BLACKSTONE).lightLevel(litBlockEmission(13)));

		blockEntityType = BlockEntityType.Builder.of(VariantFurnaceBlockEntity::new, deepslateFurnace, blackstoneFurnace).build(null);
		Quark.ZETA.registry.register(blockEntityType, "variant_furnace", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	}

	private static ToIntFunction<BlockState> litBlockEmission(int lvl) {
		return s -> s.getValue(BlockStateProperties.LIT) ? lvl : 0;
	}

}
