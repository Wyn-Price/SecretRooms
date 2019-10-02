package com.wynprice.secretrooms.server.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class SecretChestTileEntity extends SecretTileEntity implements IInventory, INamedContainerProvider {
    private final ItemStackHandler handler = new ItemStackHandler(27);
    private int numPlayersUsing;

    public SecretChestTileEntity() {
        super(SecretTileEntities.SECRET_CHEST_TILE_ENTITY);
    }

    @Override
    public void read(CompoundNBT compound) {
        this.handler.deserializeNBT(compound.getCompound("Items"));
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("Items", this.handler.serializeNBT());
        return super.write(compound);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("secretroomsmod.container.secretchest.name");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return ChestContainer.createGeneric9X3(id, inv, this);
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
            this.onOpenOrClose();
        }

    }

    private void onOpenOrClose() {
        this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockState().getBlock());
        this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockState().getBlock());
    }

    @Override
    public int getSizeInventory() {
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
    public ItemStack getStackInSlot(int index) {
        return this.handler.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.handler.getStackInSlot(index).split(count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack slot = this.handler.getStackInSlot(index);
        this.handler.setStackInSlot(index, ItemStack.EMPTY);
        return slot;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.handler.setStackInSlot(index, stack);
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.handler.getSlots(); i++) {
            this.handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
    }
}
