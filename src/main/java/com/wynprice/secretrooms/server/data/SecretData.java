package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Objects;

public class SecretData {
    private final TileEntity base;

    private BlockState blockState = Blocks.STONE.getDefaultState();
    @Nullable private CompoundNBT tileEntityNBT = null;

    private TileEntity tileEntityCache;

    public SecretData(TileEntity base) {
        this.base = base;
    }

    public CompoundNBT writeNBT(CompoundNBT tag) {
        tag.put("blockstate", NBTUtil.writeBlockState(this.getBlockState()));
        if(this.tileEntityNBT != null) {
            tag.put("tile_data", this.tileEntityNBT);
        }
        return tag;
    }

    public void readNBT(CompoundNBT tag) {
        this.setBlockState(NBTUtil.readBlockState(tag.getCompound("blockstate")));
        this.setTileEntityNBT(tag.getCompound("tile_data"));
    }

    public TileEntity getTileEntityCache() {
        if(this.tileEntityNBT != null && this.tileEntityCache == null) {
            if ((this.tileEntityCache = TileEntity.create(this.tileEntityNBT)) != null) {
                if(this.base != null) {
                    this.tileEntityCache.setPos(this.base.getPos());
                    this.tileEntityCache.setWorldAndPos(this.base.getWorld(), this.base.getPos());
                }
                this.tileEntityCache.cachedBlockState = this.blockState;
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
        if(this.blockState.getBlock() instanceof SecretBaseBlock || (this.base != null && this.blockState.isAir(this.base.getWorld(), this.base.getPos()))) {
            this.blockState = Blocks.STONE.getDefaultState();
        }
        return this.blockState;
    }

    public void setTileEntityNBT(@Nullable CompoundNBT tileEntityNBT) {
        if(tileEntityNBT != null && tileEntityNBT.isEmpty()) {
            this.tileEntityNBT = null;
        } else {
            this.tileEntityNBT = tileEntityNBT;
        }
        this.tileEntityCache = null;
        this.onChanged();
    }

    private void onChanged() {
        if(this.base != null && this.base.getWorld() != null) {
            World world = this.base.getWorld();
            if(world.isRemote) {
                this.base.requestModelDataUpdate();
                world.notifyBlockUpdate(this.base.getPos(), this.base.getBlockState(), this.base.getBlockState(), 11);
            } else {
                SUpdateTileEntityPacket supdatetileentitypacket = this.base.getUpdatePacket();
                for (ServerPlayerEntity player : ((ServerWorld) world).getPlayers()) {
                    if (supdatetileentitypacket != null) {
                        player.connection.sendPacket(supdatetileentitypacket);
                    }
                }
            }
            world.getChunkProvider().getLightManager().checkBlock(this.base.getPos());
        }
    }

    @Nullable
    public CompoundNBT getTileEntityNBT() {
        return tileEntityNBT;
    }
}
