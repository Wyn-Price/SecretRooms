package com.wynprice.secretrooms.client.world;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSettings;

public abstract class SecretDummyWorld extends ClientWorld {

    private static final Minecraft MC = Minecraft.getInstance();

    protected final ClientWorld world;

    public SecretDummyWorld(ClientWorld world) {
        super(MC.player.connection, new WorldSettings(world.getWorldInfo()), world.getDimension().getType(), 3, world.getProfiler(), MC.worldRenderer);
        this.world = world;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.world.getTileEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.world.getBlockState(pos);
    }

}