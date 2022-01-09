package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SecretBaseState extends BlockState {
    public SecretBaseState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        super(block, propertiesToValueMap, codec);
    }

    @Override
    public boolean canOcclude() {
        Block block = this.getBlock();
        if(block instanceof SecretBaseBlock) {
            Boolean value = ((SecretBaseBlock) block).getSolidValue();
            if(value != null) {
                return value;
            }
        }
        return this.getValue(SecretBaseBlock.SOLID);
    }

    @Override
    public float getDestroySpeed(BlockGetter worldIn, BlockPos pos) {
        //The min is to make sure that if the user say copied bedrock, it can still be destroyed.#
        float value = SecretBaseBlock.getValue(worldIn, pos, BlockState::getDestroySpeed, () -> super.getDestroySpeed(worldIn, pos));
        if(value > 5F) {
            return 5F;
        } else if(value < 0F) {
            return 0.5F;
        } else {
            return value;
        }
    }

    @Override
    public VoxelShape getFaceOcclusionShape(BlockGetter worldIn, BlockPos p, Direction directionIn) {
        return SecretBaseBlock.getValue(worldIn, p, (mirror, reader, pos1) -> mirror.getFaceOcclusionShape(reader, p, directionIn), () ->super.getFaceOcclusionShape(worldIn, p, directionIn));
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockGetter p_60839_, BlockPos p_60840_) {
        return super.isCollisionShapeFullBlock(p_60839_, p_60840_);
    }

    @Override
    public boolean isFaceSturdy(BlockGetter p_60660_, BlockPos p_60661_, Direction p_60662_, SupportType p_60663_) {
        return super.isFaceSturdy(p_60660_, p_60661_, p_60662_, p_60663_);
    }

    @Override
    public boolean useShapeForLightOcclusion() {
        return true;
    }
}
