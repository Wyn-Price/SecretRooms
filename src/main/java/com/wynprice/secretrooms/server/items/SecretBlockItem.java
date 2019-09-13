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
import net.minecraft.world.World;

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

        if(placedOnTileEntity instanceof SecretTileEntity) {
            SecretData data = ((SecretTileEntity) placedOnTileEntity).getData();
            placedOnState = data.getBlockState();
            placedOnTileEntity = data.getTileEntityCache();
        }

//        if(super.placeBlock(context, state.with(SecretBaseBlock.SOLID, placedOnState.isSolid()))) {
//            this.setData(context.getWorld(), context.getPos(), placedOnState, placedOnTileEntity);
//            context.getWorld().getChunkProvider().getLightManager().checkBlock(context.getPos());
//            return true;
//        }
//        return false;

        return this.doSetBlock(context.getWorld(), context.getPos(), state, placedOnState, placedOnTileEntity);
    }

    protected boolean doSetBlock(World world, BlockPos pos, BlockState state, BlockState placedOnState, TileEntity placedOnTileEntity) {
        if(world.setBlockState(pos, state.with(SecretBaseBlock.SOLID, placedOnState.isSolid()), 11)) {
            this.setData(world, pos, placedOnState, placedOnTileEntity);
            world.getChunkProvider().getLightManager().checkBlock(pos);
            return true;
        }
        return false;
    }

    protected void setData(World world, BlockPos pos, BlockState placedOnState, TileEntity placedOnTileEntity) {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof SecretTileEntity) {
            SecretData data = ((SecretTileEntity) te).getData();
            data.setBlockState(placedOnState);
            data.setTileEntityNBT(placedOnTileEntity != null ? placedOnTileEntity.serializeNBT() : null);
        }
    }
}
