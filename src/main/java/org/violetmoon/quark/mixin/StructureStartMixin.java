package org.violetmoon.quark.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.violetmoon.quark.base.handler.StructureBlockReplacementHandler;

@Mixin(StructureStart.class)
public class StructureStartMixin {

	@Shadow
	@Final
	private PiecesContainer pieceContainer;

	@Shadow
	@Final
	private Structure structure;

	@Inject(method = "placeInChunk", at = @At("HEAD"))
	public void injectReference(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource random, BoundingBox bounds, ChunkPos pos, CallbackInfo callback) {
		StructureBlockReplacementHandler.setActiveStructure(structure, pieceContainer);
	}

	@Inject(method = "placeInChunk", at = @At("RETURN"))
	public void resetReference(WorldGenLevel level, StructureManager manager, ChunkGenerator generator, RandomSource random, BoundingBox bounds, ChunkPos pos, CallbackInfo callback) {
		StructureBlockReplacementHandler.setActiveStructure(null, null);
	}

}

