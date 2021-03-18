package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.Property;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.feature.BlockBlobFeature;

import java.util.Optional;

public class SecretBaseState extends BlockState {
    public SecretBaseState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        super(block, propertiesToValueMap, codec);
    }

    @Override
    public boolean isSolid() {
        Block block = this.getBlock();
        if(block instanceof SecretBaseBlock) {
            Boolean value = ((SecretBaseBlock) block).getSolidValue();
            if(value != null) {
                return value;
            }
        }
        return this.get(SecretBaseBlock.SOLID);
    }

    @Override
    public float getBlockHardness(IBlockReader worldIn, BlockPos pos) {
        //The min is to make sure that if the user say copied bedrock, it can still be destroyed.#
        float value = SecretBaseBlock.getValue(worldIn, pos, BlockState::getBlockHardness, () -> super.getBlockHardness(worldIn, pos));
        if(value > 5F) {
            return 5F;
        } else if(value < 0F) {
            return 0.5F;
        } else {
            return value;
        }
    }

    @Override
    public VoxelShape getFaceOcclusionShape(IBlockReader worldIn, BlockPos p, Direction directionIn) {
        return SecretBaseBlock.getValue(worldIn, p, (mirror, reader, pos1) -> mirror.getFaceOcclusionShape(reader, p, directionIn), () ->super.getFaceOcclusionShape(worldIn, p, directionIn));
    }
}
