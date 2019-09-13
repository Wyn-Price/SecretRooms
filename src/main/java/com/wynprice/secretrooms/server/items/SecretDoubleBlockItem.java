package com.wynprice.secretrooms.server.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SecretDoubleBlockItem extends SecretBlockItem {
    public SecretDoubleBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        context.getWorld().setBlockState(context.getPos().up(), Blocks.AIR.getDefaultState(), 27);
        return super.placeBlock(context, state);
    }

    @Override
    protected boolean doSetBlock(World world, BlockPos pos, BlockState state, BlockState placedOnState, TileEntity placedOnTileEntity) {
        return super.doSetBlock(world, pos, state, placedOnState, placedOnTileEntity) | super.doSetBlock(world, pos.up(), state, placedOnState, placedOnTileEntity);
    }
}
