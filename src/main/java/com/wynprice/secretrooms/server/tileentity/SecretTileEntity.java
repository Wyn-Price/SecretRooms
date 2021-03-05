package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SecretTileEntity extends TileEntity {

    private final SecretData data = new SecretData(this);

    public SecretTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public SecretTileEntity() {
        super(SecretTileEntities.SECRET_TILE_ENTITY.get());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("secret_data", this.data.writeNBT(new CompoundNBT()));
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.data.readNBT(nbt.getCompound("secret_data"));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }


    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.read(state, tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, -1, this.data.writeNBT(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.data.readNBT(pkt.getNbtCompound());
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        if(this.removed) {
            return super.getModelData();
        }
        ModelDataMap.Builder builder = new ModelDataMap.Builder()
                .withInitial(SecretModelData.SRM_BLOCKSTATE, this.data.getBlockState());
        BlockState state = this.world.getBlockState(this.pos);
        if(state.getBlock() instanceof SecretBaseBlock) {
            ((SecretBaseBlock) state.getBlock()).applyExtraModelData(this.world, this.pos, state, builder);
        }
        return builder.build();
    }

    public SecretData getData() {
        return this.data;
    }
}
