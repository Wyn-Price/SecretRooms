package com.wynprice.secretrooms.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class OneWayGlassBlockstateDelegate extends BlockState {

    private final BlockState delegate;

    public OneWayGlassBlockstateDelegate(BlockState delegate) {
        super(delegate.getBlock(), delegate.getValues());
        this.delegate = delegate;
    }

    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer) {
        return true;
    }

    //Used in block#shouldSideBeRendered.
    @Override
    public VoxelShape func_215702_a(IBlockReader worldIn, BlockPos pos, Direction directionIn) {
        return VoxelShapes.empty();
    }

    public BlockState getDelegate() {
        return this.delegate;
    }
}
