package com.wynprice.secretrooms.server.items;

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
        BlockState placedOnState = context.getWorld().getBlockState(offFace);
        TileEntity placedOnTileEntity = context.getWorld().getTileEntity(offFace);

        if(super.placeBlock(context, state)) {
            TileEntity te = context.getWorld().getTileEntity(context.getPos());
            if(te instanceof SecretTileEntity) {
                SecretData data = ((SecretTileEntity) te).getData();
                data.setBlockState(placedOnState);
                data.setTileEntityNBT(placedOnTileEntity != null ? placedOnTileEntity.serializeNBT() : null);
            }
            return true;
        }
        return false;
    }
}
