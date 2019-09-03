package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SecretData {
    private final TileEntity base;

    private BlockState blockState = Blocks.STONE.getDefaultState();
    @Nullable private CompoundNBT tileEntityNBT = null;

    private TileEntity tileEntityCache;

    public SecretData(TileEntity base) {
        this.base = base;
    }

    public TileEntity getTileEntityCache() {
        if(this.tileEntityNBT != null && this.tileEntityCache == null) {
            if ((this.tileEntityCache = TileEntity.create(this.tileEntityNBT)) != null) {
                this.tileEntityCache.setPos(this.base.getPos());
                this.tileEntityCache.setWorld(this.base.getWorld());
                this.tileEntityCache.cachedBlockState = this.blockState;
            } else {
                SecretRooms6.LOGGER.warn("Unable to create Block Entity with id {}. Disregarding it from future references ", this.tileEntityNBT.getString("id"));
                this.tileEntityNBT = null;
                return null;
            }
        }
        return this.tileEntityCache;
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    public BlockState getBlockState() {
        if(this.blockState.getBlock() instanceof SecretBaseBlock) {
            this.blockState = Blocks.STONE.getDefaultState();
        }
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
