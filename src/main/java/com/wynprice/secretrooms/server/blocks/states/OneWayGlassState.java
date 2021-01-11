package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class OneWayGlassState extends BlockState {

    public OneWayGlassState(Block blockIn, ImmutableMap<IProperty<?>, Comparable<?>> properties) {
        super(blockIn, properties);
    }

    @Override
    public VoxelShape getFaceOcclusionShape(IBlockReader worldIn, BlockPos pos, Direction directionIn) {
        BlockState blockState = worldIn.getBlockState(pos.offset(directionIn));
        if(blockState.getBlock() == Blocks.GLASS) {
            return VoxelShapes.fullCube();
        }
        return super.getFaceOcclusionShape(worldIn, pos, directionIn);
    }
}
