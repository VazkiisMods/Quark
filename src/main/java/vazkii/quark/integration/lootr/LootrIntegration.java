package vazkii.quark.integration.lootr;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noobanidus.mods.lootr.init.ModBlocks;
import vazkii.quark.base.Quark;
import vazkii.quark.content.building.module.VariantChestsModule;
import vazkii.quark.integration.lootr.client.LootrVariantChestRenderer;
import vazkii.zeta.event.client.ZPreTextureStitch;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static vazkii.quark.content.building.module.VariantChestsModule.registerChests;

/**
 * @author WireSegal
 * Created at 11:40 AM on 7/3/23.
 */
public class LootrIntegration implements ILootrIntegration {

	private BlockEntityType<LootrVariantChestBlockEntity> chestTEType;
	private BlockEntityType<LootrVariantTrappedChestBlockEntity> trappedChestTEType;

	private final Map<Block, Block> lootrChests = new HashMap<>();

	private final List<Block> chests = new LinkedList<>();
	private final List<Block> trappedChests = new LinkedList<>();
	private final List<Block> allChests = new LinkedList<>();

	@Override
	public BlockEntityType<? extends ChestBlockEntity> chestTE() {
		return chestTEType;
	}

	@Override
	public BlockEntityType<? extends ChestBlockEntity> trappedChestTE() {
		return trappedChestTEType;
	}

	@Override
	@Nullable
	public Block lootrVariant(Block base) {
		return lootrChests.get(base);
	}

	@Override
	public void postRegister() {
		chestTEType = registerChests(LootrVariantChestBlockEntity::new, () -> chestTEType,
			LootrVariantChestBlock::new, LootrVariantChestBlock.Compat::new, null,
			allChests::addAll, chests::addAll);
		trappedChestTEType = registerChests(LootrVariantTrappedChestBlockEntity::new, () -> trappedChestTEType,
			LootrVariantTrappedChestBlock::new, LootrVariantTrappedChestBlock.Compat::new, null,
			allChests::addAll, trappedChests::addAll);

		for (int i = 0; i < chests.size(); i++) {
			lootrChests.put(VariantChestsModule.chests.get(i), chests.get(i));
			lootrChests.put(VariantChestsModule.trappedChests.get(i), trappedChests.get(i));
		}

		Quark.ZETA.registry.register(chestTEType, "lootr_variant_chest", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
		Quark.ZETA.registry.register(trappedChestTEType, "lootr_variant_trapped_chest", Registry.BLOCK_ENTITY_TYPE_REGISTRY);
	}

	@Override
	public void loadComplete() {
		ModBlocks.getSpecialLootChests().addAll(allChests);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		BlockEntityRenderers.register(chestTEType, LootrVariantChestRenderer::new);
		BlockEntityRenderers.register(trappedChestTEType, LootrVariantChestRenderer::new);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void stitch(ZPreTextureStitch event) {
		for (Block b : allChests)
			LootrVariantChestRenderer.accept(event, b);
	}
}
