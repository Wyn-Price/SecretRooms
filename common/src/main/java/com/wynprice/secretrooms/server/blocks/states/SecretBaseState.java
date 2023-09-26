package com.wynprice.secretrooms.server.blocks.states;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class SecretBaseState extends BlockState {
    public SecretBaseState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        super(block, propertiesToValueMap, codec);
    }


    //TODO (port) replace with mixin to shouldRenderFace
    @Override
    public boolean skipRendering(BlockState p_60720_, Direction p_60721_) {
        this.removeAllKeys();
        return super.skipRendering(p_60720_, p_60721_);
    }

    @Override
    public boolean canOcclude() {
        this.removeAllKeys();
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
        this.addOcclusionCacheKeysForRemoval(worldIn, p, directionIn);
        return SecretBaseBlock.getValue(worldIn, p, (mirror, reader, pos1) -> mirror.getFaceOcclusionShape(reader, p, directionIn), () -> super.getFaceOcclusionShape(worldIn, p, directionIn));
    }

    @Override
    public boolean emissiveRendering(BlockGetter worldIn, BlockPos pos) {
        return SecretBaseBlock.getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.emissiveRendering(reader, pos), () -> super.emissiveRendering(worldIn, pos));
    }

    @Override
    public boolean useShapeForLightOcclusion() {
        return true;
    }

    private static ThreadLocal<List<Block.BlockStatePairKey>> TO_REMOVE = ThreadLocal.withInitial(ArrayList::new);

    //This is to prevent Block#shouldRenderFace from returning false inccorectly
    //As the results of occlusion is usually cached, but in this case we don't
    //Want to cache it
    // TODO (port) move to mixins
    private void addOcclusionCacheKeysForRemoval(BlockGetter worldIn, BlockPos p, Direction directionIn) {
        BlockState other = worldIn.getBlockState(p.relative(directionIn));

        //When this is the block being checked
        Block.BlockStatePairKey thisCall = new Block.BlockStatePairKey(other, this.asState(), directionIn.getOpposite());

        //When `other` is the block being checked.
        Block.BlockStatePairKey otherCall = new Block.BlockStatePairKey(this.asState(), other, directionIn);

        List<Block.BlockStatePairKey> keys = TO_REMOVE.get();
        keys.add(thisCall);
        keys.add(otherCall);
    }

    private void removeAllKeys() {
        List<Block.BlockStatePairKey> keys = TO_REMOVE.get();
        //TODO (port) move this all to mixins
//        Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> map = Block.OCCLUSION_CACHE.get();
//
//        for (Block.BlockStatePairKey key : keys) {
//            map.removeByte(key);
//        }
//        keys.clear();
    }
}
