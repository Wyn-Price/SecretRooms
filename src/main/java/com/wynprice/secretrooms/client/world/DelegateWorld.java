package com.wynprice.secretrooms.client.world;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILightReader;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;

import javax.annotation.Nullable;

public class DelegateWorld implements ILightReader {
    private final IBlockReader world;

    public DelegateWorld(IBlockReader world) {
        this.world = world;
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
    public IFluidState getFluidState(BlockPos pos) {
        return this.world.getFluidState(pos);
    }

    @Override
    public WorldLightManager getLightManager() {
        return null;
    }

    @Override
    public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        System.out.println(Minecraft.getInstance().world.getBlockColor(blockPosIn, colorResolverIn));
        return Minecraft.getInstance().world.getBlockColor(blockPosIn, colorResolverIn);
    }
}
