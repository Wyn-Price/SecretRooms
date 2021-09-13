package com.wynprice.secretrooms.client.world;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DelegateWorld implements BlockAndTintGetter {

    private static final List<DelegateWorld> AVAILABLE = new ArrayList<>();
    public static synchronized DelegateWorld getPooled(BlockGetter reader) {
        if(AVAILABLE.isEmpty()) {
            return new DelegateWorld(reader);
        }
        DelegateWorld world = AVAILABLE.remove(0);
        //Impossible bug that sometimes decides to appear
        if(world == null) {
            return new DelegateWorld(reader);
        }
        world.use(reader);
        return world;
    }

    public static <T> Function<BlockState, T> createFunction(BlockGetter reader, BiFunction<DelegateWorld, BlockState, T> func) {
        return mirror -> {
            DelegateWorld pooled = getPooled(reader);
            T ret = func.apply(pooled, mirror);
            pooled.release();
            return ret;
        };
    }

    private BlockAndTintGetter reader;
    private BlockGetter world;

    public DelegateWorld(BlockGetter world) {
        this.use(world);
    }

    private void use(BlockGetter world) {
        this.world = world;
        if(this.world instanceof BlockAndTintGetter) {
            this.reader = (BlockAndTintGetter) this.world;
        } else {
            this.reader = null;
        }
    }

    public void release() {
        AVAILABLE.add(this);
        this.world = null;
        this.reader = null;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return SecretBaseBlock.getMirrorData(this.world, pos).map(SecretData::getTileEntityCache).orElseGet(() -> this.world.getBlockEntity(pos));
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return SecretBaseBlock.getMirrorState(this.world, pos).orElseGet(() -> this.world.getBlockState(pos));
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.world.getFluidState(pos);
    }

    @Override
    public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
        return 0;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        if(this.reader != null) {
            return this.reader.getLightEngine();
        }
        throw new IllegalStateException("Invalid Call on Delegate World, resulting in a Invalid State. WORLD: " + this.world.getClass());
    }

    @Override
    public int getBlockTint(BlockPos blockPosIn, ColorResolver colorResolverIn) {

        return this.reader == null ? 0 : this.reader.getBlockTint(blockPosIn, colorResolverIn);
    }


    @Override
    public int getHeight() {
        return this.world.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return this.world.getMinBuildHeight();
    }
}
