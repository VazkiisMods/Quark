package org.violetmoon.quark.content.building.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import org.violetmoon.quark.content.building.module.VariantFurnacesModule;

public class VariantFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

	public VariantFurnaceBlockEntity(BlockPos pos, BlockState state) {
		super(VariantFurnacesModule.blockEntityType, pos, state, RecipeType.SMELTING);
	}

	@NotNull
	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.furnace");
	}

	@NotNull
	@Override
	protected AbstractContainerMenu createMenu(int id, @NotNull Inventory playerInventory) {
		return new FurnaceMenu(id, playerInventory, this, this.dataAccess);
	}

}
