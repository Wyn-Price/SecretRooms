package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class SecretBlockItem extends BlockItem {
    public SecretBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        BlockPos offFace = context.replacingClickedOnBlock() ? context.getPos() : context.getPos().offset(context.getFace().getOpposite());
        BlockState placedOnStateRaw = context.getWorld().getBlockState(offFace);
        BlockState placedOnState = placedOnStateRaw.getBlock().getStateForPlacement(context);
        if(placedOnState == null) {
            placedOnState = placedOnStateRaw;
        }
        TileEntity placedOnTileEntity = context.getWorld().getTileEntity(offFace);

        if(super.placeBlock(context, state.with(SecretBaseBlock.SOLID, placedOnState.isSolid()))) {
            this.setData(context.getWorld().getTileEntity(context.getPos()), placedOnState, placedOnTileEntity);
            context.getWorld().getChunkProvider().getLightManager().checkBlock(context.getPos());
            return true;
        }
        return false;
    }

    private void setData(TileEntity te, BlockState placedOnState, TileEntity placedOnTileEntity) {
        if(te instanceof SecretTileEntity) {
            SecretData data = ((SecretTileEntity) te).getData();
            data.setBlockState(placedOnState);
            data.setTileEntityNBT(placedOnTileEntity != null ? placedOnTileEntity.serializeNBT() : null);
        }
    }
}
