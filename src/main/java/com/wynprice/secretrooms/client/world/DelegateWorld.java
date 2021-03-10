package com.wynprice.secretrooms.client.world;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class DelegateWorld implements IBlockDisplayReader {

    private static final List<DelegateWorld> AVAILABLE = new ArrayList<>();
    public static synchronized DelegateWorld getPooled(IBlockReader reader) {
        if(AVAILABLE.isEmpty()) {
            return new DelegateWorld(reader);
        }
        DelegateWorld world = AVAILABLE.get(0);
        world.use(reader);
        return world;
    }

    public static <T> Function<BlockState, T> createFunction(IBlockReader reader, BiFunction<DelegateWorld, BlockState, T> func) {
        return mirror -> {
            DelegateWorld pooled = getPooled(reader);
            T ret = func.apply(pooled, mirror);
            pooled.release();
            return ret;
        };
    }

    private IBlockDisplayReader reader;
    private IBlockReader world;

    public DelegateWorld(IBlockReader world) {
        this.use(world);
    }

    private void use(IBlockReader world) {
        this.world = world;
        if(this.world instanceof IBlockDisplayReader) {
            this.reader = (IBlockDisplayReader) this.world;
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
    public TileEntity getTileEntity(BlockPos pos) {
        return SecretBaseBlock.getMirrorData(this.world, pos).map(SecretData::getTileEntityCache).orElseGet(() -> this.world.getTileEntity(pos));
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
    public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_) {
        return 0;
    }

    @Override
    public WorldLightManager getLightManager() {
        if(this.reader != null) {
            return this.reader.getLightManager();
        }
        throw new IllegalStateException("Invalid Call on Delegate World, resulting in a Invalid State. WORLD: " + this.world.getClass());
    }

    @Override
    public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {

        return this.reader == null ? 0 : this.reader.getBlockColor(blockPosIn, colorResolverIn);
    }
}
