package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.blocks.OneWayGlass;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.state.Property;
import net.minecraft.util.BlockVoxelShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.Arrays;
import java.util.Optional;

public class OneWayGlassState extends SecretBaseState {

    public OneWayGlassState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        super(block, propertiesToValueMap, codec);
    }

    @Override
    public boolean isSolid() {
        //We don't want the glass to cull out other blocks, so we need to ensure that if this is from the
        //'shouldSideBeRendered' call, we act like a non solid block.
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if("net.minecraft.block.Block".equals(trace[2].getClassName()) && "shouldSideBeRendered".equals(trace[2].getMethodName())) {
            return false;
        }
        return true;
    }

    @Override
    public VoxelShape getFaceOcclusionShape(IBlockReader worldIn, BlockPos pos, Direction directionIn) {
        Optional<BlockState> mirror = SecretBaseBlock.getMirrorState(worldIn, pos);
        DelegateWorld world = DelegateWorld.getPooled(worldIn);
        VoxelShape shape = this.get(SecretBaseBlock.SOLID) &&
            mirror.isPresent() &&
            !this.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(directionIn)) &&
            mirror.get().func_242698_a(world, pos, directionIn, BlockVoxelShape.CENTER) ?
            VoxelShapes.fullCube() : VoxelShapes.empty();
        world.release();
        return shape;
    }



}
