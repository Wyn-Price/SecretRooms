package com.wynprice.secretrooms.client.world;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class SetTileEntityWorld extends SecretDummyWorld {
    private final BlockPos pos;
    private final TileEntity tileEntity;
    public SetTileEntityWorld(ClientWorld world, BlockPos pos, TileEntity tileEntity) {
        super(world);
        this.pos = pos;
        this.tileEntity = tileEntity;
    }

    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.pos.equals(pos) ? this.tileEntity : super.getTileEntity(pos);
    }
}
