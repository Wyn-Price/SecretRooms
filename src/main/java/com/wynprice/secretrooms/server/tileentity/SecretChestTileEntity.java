package com.wynprice.secretrooms.server.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class SecretChestTileEntity extends SecretTileEntity implements Container, MenuProvider {
    private final ItemStackHandler handler = new ItemStackHandler(27);
    private int numPlayersUsing;

    public SecretChestTileEntity(BlockPos pos, BlockState state) {
        super(SecretTileEntities.SECRET_CHEST_TILE_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.handler.deserializeNBT(nbt.getCompound("Items"));
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        compound.put("Items", this.handler.serializeNBT());
        return super.save(compound);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("secretroomsmod.container.secretchest.name");
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
        for (int i = 0; i < this.handler.getSlots(); i++) {
            value &= this.handler.getStackInSlot(i).isEmpty();
        }
        return value;
    }

    @Override
    public ItemStack getItem(int index) {
        return this.handler.getStackInSlot(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        return this.handler.getStackInSlot(index).split(count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack slot = this.handler.getStackInSlot(index);
        this.handler.setStackInSlot(index, ItemStack.EMPTY);
        return slot;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.handler.setStackInSlot(index, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < this.handler.getSlots(); i++) {
            this.handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
    }
}
