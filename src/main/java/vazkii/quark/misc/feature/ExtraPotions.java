package vazkii.quark.misc.feature;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreIngredient;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.world.feature.Biotite;
import vazkii.quark.world.feature.UndergroundBiomes;

public class ExtraPotions extends Feature {

	public static Potion dangerSight;

	boolean enableHaste, enableResistance, enableDangerSight;
	boolean forceQuartzForResistance, forceClownfishForDangerSight;

	@Override
	public void setupConfig() {
		enableHaste = loadPropBool("Enable Haste Potion", "", true);
		enableResistance = loadPropBool("Enable Resistance Potion", "", true);
		enableDangerSight = loadPropBool("Enable Danger Sight Potion", "", true);
		forceQuartzForResistance = loadPropBool("Force Quartz for Resistance", "Always use Quartz instead of Biotite, even if Biotite is available.", false);
		forceClownfishForDangerSight = loadPropBool("Force Clownfish for Danger Sight", "Always use Clownfish instead of Glowshroom, even if Glowshroom is available.", forceClownfishForDangerSight);
	}

	@Override
	public void postPreInit(FMLPreInitializationEvent event) {
		if(enableHaste)
			addStandardBlend(MobEffects.HASTE, Items.PRISMARINE_CRYSTALS, MobEffects.MINING_FATIGUE);

		if(enableResistance)
			addStandardBlend(MobEffects.RESISTANCE, (Biotite.biotite == null || forceQuartzForResistance) ? Items.QUARTZ : Biotite.biotite);

		if(enableDangerSight) {
			dangerSight = new PotionMod("danger_sight", false, 0x08C8E3, 1).setBeneficial();

			addStandardBlend(dangerSight, (UndergroundBiomes.glowshroom == null || forceClownfishForDangerSight) ? 
					new ItemStack(Items.FISH, 1, 2) : UndergroundBiomes.glowshroom, null, 3600, 9600, 0);
		}
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void clientTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		if(enableDangerSight && event.phase == Phase.START && mc.player != null && mc.player.getActivePotionEffect(dangerSight) != null && !mc.isGamePaused()) {
			int range = 12;
			World world = mc.world;
			Iterable<BlockPos> positions = BlockPos.getAllInBox(mc.player.getPosition().add(-range, -range, -range), mc.player.getPosition().add(range, range, range));
			
			for(BlockPos pos : positions)
				if(world.rand.nextFloat() < 0.1 && canMobsSpawnInPos(world, pos)) {
		    		float x = pos.getX() + 0.3F + world.rand.nextFloat() * 0.4F;
		        	float y = pos.getY();
		        	float z = pos.getZ() + 0.3F + world.rand.nextFloat() * 0.4F;
		            world.spawnParticle(EnumParticleTypes.SPELL_MOB, x, y, z, world.rand.nextFloat() < 0.9 ? 0 : 1, 0, 0);	
				}
		}
	}
	
	// Shamelessly stolen from BetterWithMods
	// https://github.com/BetterWithMods/BetterWithMods/blob/bf630aa1fade156ce8fae0d769ad745a4161b0ba/src/main/java/betterwithmods/event/PotionEventHandler.java
	private boolean canMobsSpawnInPos(World world, BlockPos pos) {
		if(world.isSideSolid(pos.down(), EnumFacing.UP) && !world.isBlockNormalCube(pos, false)
				&& !world.isBlockNormalCube(pos.up(), false) && !world.getBlockState(pos).getMaterial().isLiquid()) {
			IBlockState state = world.getBlockState(pos);
			
			if(ModuleLoader.isFeatureEnabled(BlackAsh.class) && state.getBlock() == BlackAsh.black_ash || world.getBlockState(pos.down(2)).getBlock() == BlackAsh.black_ash)
				return false;
			
			int lightLevel = world.getLightFor(EnumSkyBlock.BLOCK, pos);
			return lightLevel < 8 && (world.isAirBlock(pos) || state.getCollisionBoundingBox(world, pos) == null);
		}
		
		return false;
	}

	private void addStandardBlend(Potion type, Object reagent) {
		addStandardBlend(type, reagent, null);
	}

	private void addStandardBlend(Potion type, Object reagent, Potion negation) {
		addStandardBlend(type, reagent, negation, 3600, 9600, 1800);
	}

	private void addStandardBlend(Potion type, Object reagent, Potion negation, int normalTime, int longTime, int strongTime) {
		String baseName = type.getRegistryName().getResourcePath();
		boolean hasStrong = strongTime > 0;

		PotionType normalType = addPotion(new PotionEffect(type, normalTime), baseName, baseName);
		PotionType longType = addPotion(new PotionEffect(type, longTime), baseName, "long_" + baseName);
		PotionType strongType = !hasStrong ? null : addPotion(new PotionEffect(type, strongTime, 1), baseName, "strong_" + baseName);

		if(reagent instanceof Item)
			reagent = Ingredient.fromItem((Item) reagent);
		else if(reagent instanceof Block)
			reagent = Ingredient.fromStacks(ProxyRegistry.newStack((Block) reagent));
		else if(reagent instanceof ItemStack)
			reagent = Ingredient.fromStacks((ItemStack) reagent);
		else if(reagent instanceof String)
			reagent = new OreIngredient((String) reagent);
		
		if(reagent instanceof Ingredient) {
			PotionHelper.addMix(PotionTypes.AWKWARD, (Ingredient) reagent, normalType);
			PotionHelper.addMix(PotionTypes.WATER, (Ingredient) reagent, PotionTypes.MUNDANE);
		} else throw new IllegalArgumentException("Reagent can't be " + reagent.getClass());

		if(hasStrong)
			PotionHelper.addMix(normalType, Items.GLOWSTONE_DUST, strongType);
		PotionHelper.addMix(normalType, Items.REDSTONE, longType);

		if(negation != null) {
			String negationBaseName = negation.getRegistryName().getResourcePath();

			PotionType normalNegationType = addPotion(new PotionEffect(negation, normalTime), negationBaseName, negationBaseName);
			PotionType longNegationType = addPotion(new PotionEffect(negation, longTime), negationBaseName, "long_" + negationBaseName);
			PotionType strongNegationType = !hasStrong ? null : addPotion(new PotionEffect(negation, strongTime, 1), negationBaseName, "strong_" + negationBaseName);

			PotionHelper.addMix(normalType, Items.FERMENTED_SPIDER_EYE, normalNegationType);

			if(hasStrong)
				PotionHelper.addMix(strongType, Items.FERMENTED_SPIDER_EYE, strongNegationType);
			PotionHelper.addMix(longType, Items.FERMENTED_SPIDER_EYE, longNegationType);
		}
	}

	private PotionType addPotion(PotionEffect eff, String baseName, String name) {
		PotionType type = new PotionType(baseName, eff).setRegistryName(new ResourceLocation(LibMisc.MOD_ID, name));
		ProxyRegistry.register(type);

		return type;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return isClient();
	}

}
