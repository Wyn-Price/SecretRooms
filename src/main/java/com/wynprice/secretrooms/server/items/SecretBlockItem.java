package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.client.world.DummyIWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

class SecretBlockItem extends BlockItem {
    public SecretBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        BlockPos offFace = context.replacingClickedOnBlock() ? context.getClickedPos() : context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState placedOnStateRaw = context.getLevel().getBlockState(offFace);
        BlockEntity placedOnTileEntity = context.getLevel().getBlockEntity(offFace);
        if(placedOnTileEntity instanceof SecretTileEntity) {
            SecretData data = ((SecretTileEntity) placedOnTileEntity).getData();
            placedOnStateRaw = data.getBlockState();
            placedOnTileEntity = data.getTileEntityCache();
        }

        BlockState placedOnState = placedOnStateRaw.getBlock().getStateForPlacement(
            new BlockPlaceContext(
                context.getPlayer(), context.getHand(), context.getItemInHand(),
                new BlockHitResult(context.getClickLocation(), context.getClickedFace(), offFace, context.isInside())
            )
        );
        if(placedOnState == null) {
            placedOnState = placedOnStateRaw;
        }
        if(placedOnState.hasProperty(WATERLOGGED)) {
            placedOnState = placedOnState.setValue(WATERLOGGED, false);
        }

        return this.doSetBlock(context.getLevel(), context.getClickedPos(), offFace, state, placedOnState, placedOnTileEntity);
    }

    protected boolean doSetBlock(Level world, BlockPos pos, BlockPos placedOn, BlockState state, BlockState placedOnState, BlockEntity placedOnTileEntity) {
        if(world.setBlock(pos, this.getBlock() instanceof SecretBaseBlock ? ((SecretBaseBlock) this.getBlock()).getPlaceState(world, placedOn, placedOnState, state) : state, 11)) {
            placedOnState = Block.updateFromNeighbourShapes(placedOnState, new DummyIWorld(world), pos);
            this.setData(world, pos, placedOnState, placedOnTileEntity);
            world.getChunkSource().getLightEngine().checkBlock(pos);
            return true;
        }
        return false;
    }

    protected void setData(Level world, BlockPos pos, BlockState placedOnState, BlockEntity placedOnTileEntity) {
        BlockEntity te = world.getBlockEntity(pos);
        if(te instanceof SecretTileEntity) {
            SecretData data = ((SecretTileEntity) te).getData();
            data.setBlockState(placedOnState);
            data.setTileEntityNBT(placedOnTileEntity != null ? placedOnTileEntity.serializeNBT() : null);
        }
    }
}
