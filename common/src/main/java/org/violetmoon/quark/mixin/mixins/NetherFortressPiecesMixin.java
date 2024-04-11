package org.violetmoon.quark.mixin.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressPieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NetherFortressPieces.class)
public class NetherFortressPiecesMixin {
    @Mixin(NetherFortressPieces.CastleEntrance.class)
    public static class CastleEntranceMixin {
        @WrapOperation(method = "postProcess", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/levelgen/structure/structures/NetherFortressPieces$CastleEntrance;generateBox(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;IIIIIILnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Z)V",
                ordinal = 11
            )
        )
        private void quark$fixFenceConnectionInPostProcess(NetherFortressPieces.CastleEntrance instance, WorldGenLevel pLevel, BoundingBox pBox, int pXMin, int pYMin, int pZMin, int pXMax, int pYMax, int pZMax, BlockState pBoundaryBlockState, BlockState pInsideBlockState, boolean pExistingOnly, Operation<Void> original) {
            instance.generateBox(pLevel, pBox, pXMin, pYMin, pZMin, pXMax, pYMax, pZMax, pBoundaryBlockState.setValue(FenceBlock.WEST, Boolean.TRUE).setValue(FenceBlock.EAST, Boolean.TRUE), pInsideBlockState, pExistingOnly);
        }
    }
}
