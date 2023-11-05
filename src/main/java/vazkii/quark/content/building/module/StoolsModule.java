package vazkii.quark.content.building.module;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.base.Quark;
import vazkii.quark.content.building.block.StoolBlock;
import vazkii.quark.content.building.client.render.entity.StoolEntityRenderer;
import vazkii.quark.content.building.entity.Stool;
import vazkii.zeta.client.event.ZClientSetup;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.ZRightClickBlock;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.bus.PlayEvent;
import vazkii.zeta.module.ZetaLoadModule;
import vazkii.zeta.module.ZetaModule;
import vazkii.zeta.util.Hint;

@ZetaLoadModule(category = "building")
public class StoolsModule extends ZetaModule {

	public static EntityType<Stool> stoolEntity;
	
	@Hint TagKey<Item> stoolsTag;

	@LoadEvent
	public final void register(ZRegister event) {
		for(DyeColor dye : DyeColor.values())
			new StoolBlock(this, dye);

		stoolEntity = EntityType.Builder.of(Stool::new, MobCategory.MISC)
				.sized(6F / 16F, 0.5F)
				.clientTrackingRange(3)
				.updateInterval(Integer.MAX_VALUE) // update interval
				.setShouldReceiveVelocityUpdates(false)
				.setCustomClientFactory((spawnEntity, world) -> new Stool(stoolEntity, world))
				.build("stool");
		Quark.ZETA.registry.register(stoolEntity, "stool", Registry.ENTITY_TYPE_REGISTRY);
	}
	
	@LoadEvent
	public final void setup(ZCommonSetup event) {
		stoolsTag = ItemTags.create(new ResourceLocation(Quark.MOD_ID, "stools"));
	}

	@PlayEvent
	public void itemUsed(ZRightClickBlock event) {
		if(event.getEntity().isShiftKeyDown() && event.getItemStack().getItem() instanceof BlockItem && event.getFace() == Direction.UP) {
			BlockState state = event.getLevel().getBlockState(event.getPos());
			if(state.getBlock() instanceof StoolBlock stool)
				stool.blockClicked(event.getLevel(), event.getPos());
		}
	}

	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(stoolEntity, StoolEntityRenderer::new);
	}

}
