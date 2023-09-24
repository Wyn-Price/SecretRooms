package com.wynprice.secretrooms.server.items;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import net.minecraft.world.item.Item.Properties;

public class SecretDoubleBlockItem extends SecretBlockItem {
    public SecretDoubleBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        context.getLevel().setBlock(context.getClickedPos().above(), Blocks.AIR.defaultBlockState(), 27);
        return super.placeBlock(context, state);
    }

    @Override
    protected boolean doSetBlock(Level world, BlockPos pos, BlockPos placedOn, BlockState state, BlockState placedOnState, BlockEntity placedOnTileEntity) {
        return super.doSetBlock(world, pos, placedOn, state, placedOnState, placedOnTileEntity) | super.doSetBlock(world, pos.above(), placedOn, state, placedOnState, placedOnTileEntity);
    }
}
