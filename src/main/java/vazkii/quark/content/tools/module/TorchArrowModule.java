package vazkii.quark.content.tools.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import vazkii.quark.base.Quark;
import vazkii.quark.base.item.QuarkArrowItem;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.client.render.entity.TorchArrowRenderer;
import vazkii.quark.content.tools.entity.TorchArrow;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "tools")
public class TorchArrowModule extends ZetaModule {

	@Config public static boolean extinguishOnMiss = false;
	
	public static EntityType<TorchArrow> torchArrowType;
	
	@Hint public static Item torch_arrow;

	public static final TagKey<Item> ignoreMultishot = ItemTags.create(new ResourceLocation( "quark:ignore_multishot"));
	
	@LoadEvent
	public final void register(ZRegister event) {
		torch_arrow = new QuarkArrowItem.Impl("torch_arrow", this, (level, stack, living) -> new TorchArrow(level, living));
		
		torchArrowType = EntityType.Builder.<TorchArrow>of(TorchArrow::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(4)
				.updateInterval(20) // update interval
				.build("torch_arrow");
		Quark.ZETA.registry.register(torchArrowType, "torch_arrow", Registry.ENTITY_TYPE_REGISTRY);
	}
	
	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(torchArrowType, TorchArrowRenderer::new);
	}
	
}
