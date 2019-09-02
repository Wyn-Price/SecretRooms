package com.wynprice.secretrooms.client.chunkrender;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;

import javax.annotation.Nullable;

public class DelegateChunkRenderCache extends ChunkRenderCache {

    private final ChunkRenderCache cache;

    public DelegateChunkRenderCache(World world, ChunkRenderCache cache) {
        super(world, 0, 0, new Chunk[][]{{new EmptyChunk(world, new ChunkPos(0, 0))}}, BlockPos.ZERO, BlockPos.ZERO);
        this.cache = cache;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        BlockState state = this.cache.getBlockState(pos);
        if(state.getBlock() instanceof SecretBaseBlock) {
            TileEntity te = this.cache.getTileEntity(pos);
            if(te instanceof SecretTileEntity) {
                return ((SecretTileEntity) te).getData().getBlockState();
            }
        }

        return state;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType type) {
        TileEntity te = this.cache.getTileEntity(pos, type);
        if(te instanceof SecretTileEntity && this.getBlockState(pos).getBlock() instanceof SecretBaseBlock) {
            return ((SecretTileEntity) te).getData().getTileEntityCache();
        }
        return te;
    }


    @Override
    public int getCombinedLight(BlockPos pos, int minLight) {
        return this.cache.getCombinedLight(pos, minLight);
    }

    @Override
    public int getLightValue(BlockPos pos) {
        return this.cache.getLightValue(pos);
    }


    @Override
    public int getMaxLightLevel() {
        return this.cache.getMaxLightLevel();
    }

    @Override
    public int getHeight() {
        return this.cache.getHeight();
    }

    @Override
    public BlockRayTraceResult func_217296_a(Vec3d p_217296_1_, Vec3d p_217296_2_, BlockPos p_217296_3_, VoxelShape p_217296_4_, BlockState p_217296_5_) {
        return this.cache.func_217296_a(p_217296_1_, p_217296_2_, p_217296_3_, p_217296_4_, p_217296_5_);
    }

    @Override
    public boolean isSkyLightMax(BlockPos pos) {
        return this.cache.isSkyLightMax(pos);
    }

    @Override
    public IFluidState getFluidState(BlockPos pos) {
        return this.cache.getFluidState(pos);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return this.cache.getBiome(pos);
    }

    @Override
    public BlockRayTraceResult rayTraceBlocks(RayTraceContext context) {
        return this.cache.rayTraceBlocks(context);
    }

    @Override
    public int getLightFor(LightType type, BlockPos pos) {
        return this.cache.getLightFor(type, pos);
    }
}
