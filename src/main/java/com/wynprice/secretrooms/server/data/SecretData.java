package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class SecretData {
    private BlockState blockState = Blocks.STONE.getDefaultState();
    @Nullable private CompoundNBT tileEntityNBT = null;

    private TileEntity tileEntityCache;

    public TileEntity getTileEntityCache() {
        if(this.tileEntityNBT != null && this.tileEntityCache == null && (this.tileEntityCache = TileEntity.create(this.tileEntityNBT)) == null) {
            SecretRooms6.LOGGER.warn("Unable to create Block Entity with id {}. Disregarding it from future references ", this.tileEntityNBT.getString("id"));
            this.tileEntityNBT = null;
            return null;
        }
        return this.tileEntityCache;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public void setTileEntityNBT(@Nullable CompoundNBT tileEntityNBT) {
        this.tileEntityNBT = tileEntityNBT;
    }

    @Nullable
    public CompoundNBT getTileEntityNBT() {
        return this.tileEntityNBT;
    }
}
