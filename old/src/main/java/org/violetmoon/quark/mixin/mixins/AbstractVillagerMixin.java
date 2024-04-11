package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.experimental.hax.PseudoAccessorMerchantOffer;
import org.violetmoon.quark.content.experimental.module.VillagerRerollingReworkModule;

@Mixin(AbstractVillager.class)
public class AbstractVillagerMixin {

	@Inject(method = "addOffersFromItemListings", at = @At("HEAD"))
	public void replaceRandom(MerchantOffers offers, VillagerTrades.ItemListing[] listings, int toGenerate, CallbackInfo ci, @Share("newRandom") LocalRef<RandomSource> ref) {
		AbstractVillager villager = (AbstractVillager) (Object) this;
		if(VillagerRerollingReworkModule.shouldUseSeededRandom(villager, offers)) {
			ref.set(VillagerRerollingReworkModule.seededRandomForVillager(villager));
		}
	}

	@ModifyExpressionValue(
		method = "addOffersFromItemListings",
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/AbstractVillager;random:Lnet/minecraft/util/RandomSource;", opcode = Opcodes.GETFIELD)
	)
	public RandomSource replaceRandom(RandomSource original, MerchantOffers offers, @Share("newRandom") LocalRef<RandomSource> ref) {
		if(VillagerRerollingReworkModule.staticEnabled) {
			RandomSource random = ref.get();
			if(random != null)
				return random;
		}

		return original;
	}

	@ModifyExpressionValue(method = "addOffersFromItemListings", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/VillagerTrades$ItemListing;getOffer(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/item/trading/MerchantOffer;"))
	public MerchantOffer setTierForOffer(MerchantOffer newOffer) {
		if(newOffer != null) {
			AbstractVillager villager = (AbstractVillager) (Object) this;
			if(VillagerRerollingReworkModule.canUseSeededRandom(villager)) {
				VillagerData villagerData = ((VillagerDataHolder) villager).getVillagerData();
				((PseudoAccessorMerchantOffer) newOffer).quark$setTier(villagerData.getLevel());
			} else {
				((PseudoAccessorMerchantOffer) newOffer).quark$setTier(-1);
			}
		}

		return newOffer;
	}

}
