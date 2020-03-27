package vazkii.quark.building.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import vazkii.arl.interf.IBlockItemProvider;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.module.Module;
import vazkii.quark.building.block.VariantChestBlock.Compat;
import vazkii.quark.building.module.VariantChestsModule.IChestTextureProvider;
import vazkii.quark.building.tile.VariantTrappedChestTileEntity;

@OnlyIn(value = Dist.CLIENT, _interface = IBlockItemProvider.class)
public class VariantTrappedChestBlock extends ChestBlock implements IBlockItemProvider, IQuarkBlock, IChestTextureProvider {

	public final String type;
	private final Module module;
	private BooleanSupplier enabledSupplier = () -> true;

	private String path;
	
	public VariantTrappedChestBlock(String type, Module module, Supplier<TileEntityType<? extends ChestTileEntity>> supplier, Properties props) {
		super(props, supplier);
		RegistryHelper.registerBlock(this, type + "_trapped_chest");
		RegistryHelper.setCreativeTab(this, ItemGroup.REDSTONE);

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
		if(module.enabled || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public VariantTrappedChestBlock setCondition(BooleanSupplier enabledSupplier) {
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
		return new VariantTrappedChestTileEntity();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockItem provideItemBlock(Block block, Item.Properties props) {
		VariantChestBlock.setISTER(props, block);
		return new BlockItem(block, props);
	}

	public static class Compat extends VariantTrappedChestBlock {

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
		return true;
	}

	// VANILLA TrappedChestBlock copy

	@Override
	protected Stat<ResourceLocation> getOpenStat() {
		return Stats.CUSTOM.get(Stats.TRIGGER_TRAPPED_CHEST);
	}
	
	@Override
	public boolean canProvidePower(BlockState p_149744_1_) {
		return true;
	}

	@Override
	public int getWeakPower(BlockState p_180656_1_, IBlockReader p_180656_2_, BlockPos p_180656_3_, Direction p_180656_4_) {
		return MathHelper.clamp(ChestTileEntity.getPlayersUsing(p_180656_2_, p_180656_3_), 0, 15);
	}

	@Override
	public int getStrongPower(BlockState p_176211_1_, IBlockReader p_176211_2_, BlockPos p_176211_3_, Direction p_176211_4_) {
		return p_176211_4_ == Direction.UP ? p_176211_1_.getWeakPower(p_176211_2_, p_176211_3_, p_176211_4_) : 0;
	}

}
