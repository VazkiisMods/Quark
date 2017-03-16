package vazkii.quark.world.block;

import java.util.function.Supplier;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.block.BlockMetaVariants;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.world.feature.UndergroundBiomes;

public class BlockBiomeCobblestone extends BlockMetaVariants implements IQuarkBlock {

	public BlockBiomeCobblestone() {
		super("biome_cobblestone", Material.ROCK, Variants.class);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}
	
	@Override
	public boolean shouldDisplayVariant(int variant) {
		return Variants.class.getEnumConstants()[variant].isEnabled();
	}

	public enum Variants implements EnumBase {
		FIRE_STONE(() -> UndergroundBiomes.firestoneEnabled),
		ICY_STONE(() -> UndergroundBiomes.icystoneEnabled);
		
		private Variants(Supplier<Boolean> enabledCond) {
			this.enabledCond = enabledCond;
		}
		
		private final Supplier<Boolean> enabledCond;
		
		public boolean isEnabled() {
			return enabledCond.get();
		}
	}


}
