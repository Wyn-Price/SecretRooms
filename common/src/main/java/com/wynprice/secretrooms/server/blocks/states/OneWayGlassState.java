package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class OneWayGlassState extends SecretBaseState {

    public OneWayGlassState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        super(block, propertiesToValueMap, codec);
    }

    @Override
    public boolean canOcclude() {
        //We don't want the glass to cull out other blocks, so we need to ensure that if this is from the
        //'shouldRenderFace' call, we act like a non solid block.
        StackTraceElement trace = Thread.currentThread().getStackTrace()[2];
        if("net.minecraft.world.level.block.Block".equals(trace.getClassName()) && "shouldRenderFace".equals(trace.getMethodName())) {
            return false;
        }
        return true;
    }

    @Override
    public VoxelShape getFaceOcclusionShape(BlockGetter worldIn, BlockPos pos, Direction directionIn) {
        Optional<BlockState> mirror = SecretBaseBlock.getMirrorState(worldIn, pos);
        DelegateWorld world = DelegateWorld.getPooled(worldIn);
        VoxelShape shape = this.getValue(SecretBaseBlock.SOLID) &&
            mirror.isPresent() &&
            !this.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(directionIn)) &&
            mirror.get().isFaceSturdy(world, pos, directionIn, SupportType.CENTER) ?
            Shapes.block() : Shapes.empty();
        world.release();
        return shape;
    }



}
