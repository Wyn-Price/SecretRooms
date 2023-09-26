package com.wynprice.secretrooms.server.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collections;

public class SecretChestTileEntity extends SecretTileEntity implements Container, MenuProvider {
    private final NonNullList<ItemStack> stacks = NonNullList.withSize(27, ItemStack.EMPTY);
    private int numPlayersUsing;

    public SecretChestTileEntity(BlockPos pos, BlockState state) {
        super(SecretTileEntities.SECRET_CHEST_TILE_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        ContainerHelper.loadAllItems(nbt, this.stacks);
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        ContainerHelper.saveAllItems(compound, this.stacks);
        super.saveAdditional(compound);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("secretroomsmod.container.secretchest.name");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return ChestMenu.threeRows(id, inv, this);
    }

    @Override
    public void startOpen(Player player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    @Override
    public void stopOpen(Player player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    private void onOpenOrClose() {
        this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
    }

    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        boolean value = true;
        for (ItemStack stack : this.stacks) {
            value &= stack.isEmpty();
        }
        return value;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.stacks.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return this.stacks.get(index).split(count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack slot = this.stacks.get(index);
        this.stacks.set(index, ItemStack.EMPTY);
        return slot;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.stacks.set(index, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        Collections.fill(this.stacks, ItemStack.EMPTY);
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
    }
}
