package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SecretData {

    @Nullable
    private final BlockEntity base;

    private BlockState blockState = Blocks.STONE.defaultBlockState();
    @Nullable private CompoundTag tileEntityNBT = null;

    private BlockEntity tileEntityCache;

    public SecretData(BlockEntity base) {
        this.base = base;
    }

    public CompoundTag writeNBT(CompoundTag tag) {
        tag.put("blockstate", NbtUtils.writeBlockState(this.getBlockState()));
        if(this.tileEntityNBT != null) {
            tag.put("tile_data", this.tileEntityNBT);
        }
        return tag;
    }

    public void readNBT(CompoundTag tag) {
        this.setBlockState(NbtUtils.readBlockState(tag.getCompound("blockstate")));
        this.setTileEntityNBT(tag.getCompound("tile_data"));
    }

    public BlockEntity getTileEntityCache() {
        if(this.tileEntityNBT != null && this.tileEntityCache == null) {
            if ((this.tileEntityCache = BlockEntity.loadStatic(this.base.getBlockPos(), this.blockState, this.tileEntityNBT)) != null) {
                if(this.base != null) {
                    this.tileEntityCache.setLevel(this.base.getLevel());
                }
                this.tileEntityCache.blockState = this.blockState;
            } else {
                SecretRooms6.LOGGER.warn("Unable to create Block Entity with id {}. Disregarding it from future references ", this.tileEntityNBT.getString("id"));
                this.tileEntityNBT = null;
            }
        }
        return this.tileEntityCache;
    }

    public void setFrom(SecretData data) {
        this.setBlockState(data.getBlockState());
        this.setTileEntityNBT(data.getTileEntityNBT());
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
        this.onChanged();
    }

    public BlockState getBlockState() {
        if(this.blockState.getBlock() instanceof SecretBaseBlock || (this.base != null && this.blockState.isAir())) {
            this.blockState = Blocks.STONE.defaultBlockState();
        }
        return this.blockState;
    }

    public void setTileEntityNBT(@Nullable CompoundTag tileEntityNBT) {
        if(tileEntityNBT != null && tileEntityNBT.isEmpty()) {
            this.tileEntityNBT = null;
        } else {
            this.tileEntityNBT = tileEntityNBT;
        }
        this.tileEntityCache = null;
        this.onChanged();
    }

    private void onChanged() {
        if(this.base != null && this.base.getLevel() != null) {
            Level world = this.base.getLevel();
            if(world.isClientSide) {
                this.base.requestModelDataUpdate();
                world.sendBlockUpdated(this.base.getBlockPos(), this.base.getBlockState(), this.base.getBlockState(), 11);
            } else {
                ClientboundBlockEntityDataPacket supdatetileentitypacket = this.base.getUpdatePacket();
                for (ServerPlayer player : ((ServerLevel) world).players()) {
                    if (supdatetileentitypacket != null) {
                        player.connection.send(supdatetileentitypacket);
                    }
                }
            }
            world.getChunkSource().getLightEngine().checkBlock(this.base.getBlockPos());
        }
    }

    @Nullable
    public CompoundTag getTileEntityNBT() {
        return tileEntityNBT;
    }
}
