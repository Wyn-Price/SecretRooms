package com.wynprice.secretrooms.client.model;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class MappedModelBlockstateDelegate extends BlockState {
    private final BlockState delegate;

    public MappedModelBlockstateDelegate(BlockState delegate) {
        super(delegate.getBlock(), delegate.getValues());
        this.delegate = delegate;
    }

    //Used in block#shouldSideBeRendered.
    @Override
    public VoxelShape func_215702_a(IBlockReader worldIn, BlockPos pos, Direction directionIn) {
        BlockState blockState = worldIn.getBlockState(pos.offset(directionIn));
        if(Block.hasSolidSide(blockState, worldIn, pos, directionIn.getOpposite())) {
            return VoxelShapes.empty();
        }
        return VoxelShapes.fullCube();
    }

    public BlockState getDelegate() {
        return this.delegate;
    }
}
