package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.stream.Stream;

public class OneWayGlassState extends BlockState {

    public OneWayGlassState(Block blockIn, ImmutableMap<Property<?>, Comparable<?>> properties) {
        super(blockIn, properties, new MapCodec<BlockState>() {
            @Override
            public <T> RecordBuilder<T> encode(BlockState input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return null;
            }

            @Override
            public <T> DataResult<BlockState> decode(DynamicOps<T> ops, MapLike<T> input) {
                return null;
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return null;
            }
        });
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
