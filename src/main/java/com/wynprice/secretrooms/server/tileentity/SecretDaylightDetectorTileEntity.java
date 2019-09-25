package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.server.blocks.SecretDaylightDetector;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;

import java.util.Objects;

public class SecretDaylightDetectorTileEntity extends SecretTileEntity implements ITickableTileEntity {
    public SecretDaylightDetectorTileEntity() {
        super(SecretTileEntities.SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY);
    }

    @Override
    public void tick() {
        if (this.world != null && !this.world.isRemote && this.world.getGameTime() % 20L == 0L) {
            BlockState state = this.getBlockState();
            if(state.getBlock() instanceof SecretDaylightDetector) {
                SecretDaylightDetector.updatePower(state, Objects.requireNonNull(this.world), this.pos);
            }
        }
    }
}
