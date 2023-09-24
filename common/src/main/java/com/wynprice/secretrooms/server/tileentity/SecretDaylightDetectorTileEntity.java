package com.wynprice.secretrooms.server.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SecretDaylightDetectorTileEntity extends SecretTileEntity {

    public SecretDaylightDetectorTileEntity(BlockPos pos, BlockState state) {
        super(SecretTileEntities.SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY.get(), pos, state);
    }
}
