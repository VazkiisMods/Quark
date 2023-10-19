package vazkii.quark.content.tweaks.module;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.DyeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.hint.Hint;
import vazkii.quark.content.tweaks.client.render.entity.DyedItemFrameRenderer;
import vazkii.quark.content.tweaks.entity.DyedItemFrame;
import vazkii.zeta.event.ZRegister;
import vazkii.zeta.event.bus.LoadEvent;
import vazkii.zeta.event.client.ZAddModels;
import vazkii.zeta.event.client.ZClientSetup;

@LoadModule(category = "tweaks", hasSubscriptions = true)
public class DyeableItemFramesModule extends QuarkModule {

	public static EntityType<DyedItemFrame> entityType;
	
	@Hint(key = "item_frame_dyeing") 
	List<Item> itemFrames = Arrays.asList(Items.ITEM_FRAME, Items.GLOW_ITEM_FRAME);

	@LoadEvent
	public final void register(ZRegister event) {
		entityType = EntityType.Builder.<DyedItemFrame>of(DyedItemFrame::new, MobCategory.MISC)
				.sized(0.5F, 0.5F)
				.clientTrackingRange(10)
				.updateInterval(Integer.MAX_VALUE) // update interval
				.setShouldReceiveVelocityUpdates(false)
				.setCustomClientFactory((spawnEntity, world) -> new DyedItemFrame(entityType, world))
				.build("dyed_item_frame");
		Quark.ZETA.registry.register(entityType, "dyed_item_frame", Registry.ENTITY_TYPE_REGISTRY);

		DyeHandler.addDyeable(Items.ITEM_FRAME, this);
		DyeHandler.addDyeable(Items.GLOW_ITEM_FRAME, this);
	}

	@LoadEvent
	@OnlyIn(Dist.CLIENT)	
	public void registerAdditionalModels(ZAddModels event) {
		event.register(new ModelResourceLocation(Quark.MOD_ID, "extra/dyed_item_frame", "inventory"));
		event.register(new ModelResourceLocation(Quark.MOD_ID, "extra/dyed_item_frame_map", "inventory"));
	}
	
	@LoadEvent
	public final void clientSetup(ZClientSetup event) {
		EntityRenderers.register(entityType, DyedItemFrameRenderer::new);
	}

	@SubscribeEvent
	public void onUse(PlayerInteractEvent.RightClickBlock event) {
		Player player = event.getEntity();
		InteractionHand hand = event.getHand();
		ItemStack stack = player.getItemInHand(hand);

		if((stack.is(Items.ITEM_FRAME) || stack.is(Items.GLOW_ITEM_FRAME)) && DyeHandler.isDyed(stack)) {
			BlockHitResult blockhit = event.getHitVec();
			UseOnContext context = new UseOnContext(player, hand, blockhit);

			Level level = player.level;
			BlockPos pos = event.getPos();
			BlockState state = level.getBlockState(pos);
			
			InteractionResult result = player.isCrouching() ? InteractionResult.PASS : state.use(level, player, hand, blockhit); 
			if(result == InteractionResult.PASS)
				result = useOn(context);
			
			if(result != InteractionResult.PASS) {
				event.setCanceled(true);
				event.setCancellationResult(result);	
			}
		}
	}

	// Copy of the logic from HangingEntityItem from here on out
	private InteractionResult useOn(UseOnContext context) {
		BlockPos blockpos = context.getClickedPos();
		Direction direction = context.getClickedFace();
		BlockPos blockpos1 = blockpos.relative(direction);
		Player player = context.getPlayer();
		ItemStack itemstack = context.getItemInHand();

		if(player != null && !mayPlace(player, direction, itemstack, blockpos1))
			return InteractionResult.FAIL;
		
		Level level = context.getLevel();
		HangingEntity hangingentity = new DyedItemFrame(level, blockpos1, direction, DyeHandler.getDye(itemstack), itemstack.is(Items.GLOW_ITEM_FRAME));

		CompoundTag compoundtag = itemstack.getTag();
		if(compoundtag != null)
			EntityType.updateCustomEntityTag(level, player, hangingentity, compoundtag);

		if(hangingentity.survives()) {
			if(!level.isClientSide) {
				hangingentity.playPlacementSound();
				level.gameEvent(player, GameEvent.ENTITY_PLACE, hangingentity.position());
				level.addFreshEntity(hangingentity);
			}

			if(!player.isCreative())
				itemstack.shrink(1);
			
			return InteractionResult.sidedSuccess(level.isClientSide);
		} 
			
		return InteractionResult.CONSUME;
	}

	protected boolean mayPlace(Player player, Direction direction, ItemStack stack, BlockPos pos) {
		return !player.level.isOutsideBuildHeight(pos) && player.mayUseItemAt(pos, direction, stack);
	}

}
