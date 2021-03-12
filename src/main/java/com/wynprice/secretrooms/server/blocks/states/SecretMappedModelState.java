package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SecretMappedModelState extends SecretBaseState {
    public SecretMappedModelState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        super(block, propertiesToValueMap, codec);
    }

    @Override
    public VoxelShape getFaceOcclusionShape(IBlockReader worldIn, BlockPos p, Direction directionIn) {
        return VoxelShapes.getFaceShape(this.getRenderShapeTrue(worldIn, p), directionIn);
    }
}
