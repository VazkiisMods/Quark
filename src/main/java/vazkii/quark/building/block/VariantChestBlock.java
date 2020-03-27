package vazkii.quark.building.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import vazkii.arl.interf.IBlockItemProvider;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.Module;
import vazkii.quark.building.client.render.VariantChestTileEntityRenderer;
import vazkii.quark.building.module.VariantChestsModule.IChestTextureProvider;
import vazkii.quark.building.tile.VariantChestTileEntity;

@OnlyIn(value = Dist.CLIENT, _interface = IBlockItemProvider.class)
public class VariantChestBlock extends ChestBlock implements IBlockItemProvider, IQuarkBlock, IChestTextureProvider {

	public final String type;
	private final Module module;
	private BooleanSupplier enabledSupplier = () -> true;
	
	private String path;

	public VariantChestBlock(String type, Module module, Supplier<TileEntityType<? extends ChestTileEntity>> supplier, Properties props) {
		super(props, supplier);
		RegistryHelper.registerBlock(this, type + "_chest");
		RegistryHelper.setCreativeTab(this, ItemGroup.DECORATIONS);
		
		this.type = type;
		this.module = module;
		
		path = (this instanceof Compat ? "compat/" : "") + type + "/";
	}
	
	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return false;
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public VariantChestBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public Module getModule() {
		return module;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new VariantChestTileEntity();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setISTER(Item.Properties props, Block block) {
		props.setISTER(() -> () -> new ItemStackTileEntityRenderer() {
			private final TileEntity tile = new VariantChestTileEntity();
			
			public void render(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int x, int y) {
				VariantChestTileEntityRenderer.invBlock = block;
	            TileEntityRendererDispatcher.instance.renderItem(tile, matrix, buffer, x, y);
	            VariantChestTileEntityRenderer.invBlock = null;
			}
			
		});
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockItem provideItemBlock(Block block, Item.Properties props) {
		setISTER(props, block);
		return new BlockItem(block, props);
	}
	
	public static class Compat extends VariantChestBlock {

		public Compat(String type, String mod, Module module, Supplier<TileEntityType<? extends ChestTileEntity>> supplier, Properties props) {
			super(type, module, supplier, props);
			setCondition(() -> ModList.get().isLoaded(mod));
		}
		
	}

	@Override
	public String getChestTexturePath() {
		return "model/chest/" + path;
	}

	@Override
	public boolean isTrap() {
		return false;
	}
	
}
