package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.platform.SecretRoomsServices;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SecretTileEntity extends BlockEntity {

    private final SecretData data = new SecretData(this);

    public SecretTileEntity(BlockPos p_155115_, BlockState p_155116_) {
        super(SecretTileEntities.SECRET_TILE_ENTITY.get(), p_155115_, p_155116_);
    }

    public SecretTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.put("secret_data", this.data.writeNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.data.readNBT(nbt.getCompound("secret_data"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void requestModelDataUpdateIfPossible() {
        SecretRoomsServices.PLATFORM.updateModelData(this);
    }



//    @Nonnull
//    @Override
//    public IModelData getModelData() {
//        if(this.remove) {
//            return super.getModelData();
//        }
//        ModelDataMap.Builder builder = new ModelDataMap.Builder()
//                .withInitial(SecretModelData.SRM_BLOCKSTATE, this.data.getBlockState());
//        BlockState state = this.level.getBlockState(this.worldPosition);
//        if(state.getBlock() instanceof SecretBaseBlock) {
//            ((SecretBaseBlock) state.getBlock()).applyExtraModelData(this.level, this.worldPosition, state, builder);
//        }
//        return builder.build();
//    }

    public SecretData getData() {
        return this.data;
    }
}
