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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.building.block.StoolBlock;
import vazkii.quark.content.building.client.render.entity.StoolEntityRenderer;
import vazkii.quark.content.building.entity.Stool;
import vazkii.zeta.event.ZCommonSetup;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "building", hasSubscriptions = true)
public class StoolsModule extends QuarkModule {

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

	@SubscribeEvent
	public void itemUsed(RightClickBlock event) {
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
