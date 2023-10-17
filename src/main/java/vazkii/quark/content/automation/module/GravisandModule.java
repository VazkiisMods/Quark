package vazkii.quark.content.automation.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.automation.block.GravisandBlock;
import vazkii.quark.content.automation.entity.Gravisand;

@LoadModule(category = ModuleCategory.AUTOMATION)
public class GravisandModule extends QuarkModule {

	public static EntityType<Gravisand> gravisandType;

	@Hint public static Block gravisand;

	@Override
	public void register() {
		gravisand = new GravisandBlock("gravisand", this, CreativeModeTab.TAB_REDSTONE, Block.Properties.copy(Blocks.SAND));

		gravisandType = EntityType.Builder.<Gravisand>of(Gravisand::new, MobCategory.MISC)
				.sized(0.98F, 0.98F)
				.clientTrackingRange(10)
				.updateInterval(20) // update interval
				.setCustomClientFactory((spawnEntity, world) -> new Gravisand(gravisandType, world))
				.build("gravisand");
		Quark.ZETA.registry.register(gravisandType, "gravisand", Registry.ENTITY_TYPE_REGISTRY);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		EntityRenderers.register(gravisandType, FallingBlockRenderer::new);
	}
}
