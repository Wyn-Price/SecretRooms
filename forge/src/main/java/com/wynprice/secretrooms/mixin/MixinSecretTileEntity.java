package com.wynprice.secretrooms.mixin;

import com.wynprice.secretrooms.server.SecretModelData;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;

@Mixin(SecretTileEntity.class)
public abstract class MixinSecretTileEntity extends BlockEntity {

    public MixinSecretTileEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Final
    @Shadow
    private SecretData data;

    @Nonnull
    @Override
    public ModelData getModelData() {
        if(this.remove) {
            return super.getModelData();
        }
        ModelData.Builder builder = ModelData.builder()
                .with(SecretModelData.SRM_BLOCKSTATE, this.data.getBlockState());

        BlockState state = this.level.getBlockState(this.worldPosition);
        if(state.getBlock() instanceof SecretBaseBlock) {
            ((SecretBaseBlock) state.getBlock()).getMappedModelState(this.level, this.worldPosition, state)
                    .ifPresent(mapState -> builder.with(SecretModelData.MODEL_MAP_STATE, mapState));
        }
        return builder.build();
    }
}
