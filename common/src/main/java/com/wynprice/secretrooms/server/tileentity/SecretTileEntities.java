package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.platform.SecretRoomsServices;
import com.wynprice.secretrooms.server.registry.RegistryHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

@SuppressWarnings("unchecked")
public class SecretTileEntities {

    public static final RegistryHolder<BlockEntityType<?>> REGISTRY = SecretRoomsServices.PLATFORM.createBlockEntityRegistryHolder();

    public static final Supplier<BlockEntityType<SecretTileEntity>> SECRET_TILE_ENTITY = REGISTRY.register("secret_tile_entity", () ->
        SecretRoomsServices.PLATFORM.createBlockEntityType(SecretTileEntity::new,
            GHOST_BLOCK, SECRET_STAIRS, SECRET_LEVER, SECRET_REDSTONE, ONE_WAY_GLASS, SECRET_WOODEN_BUTTON,
            SECRET_STONE_BUTTON, SECRET_PRESSURE_PLATE, SECRET_PLAYER_PRESSURE_PLATE, SECRET_DOOR, SECRET_IRON_DOOR,
            SECRET_TRAPDOOR, SECRET_IRON_TRAPDOOR, SECRET_OBSERVER, SECRET_CLAMBER
        )
    );

    public static final Supplier<BlockEntityType<SecretChestTileEntity>> SECRET_CHEST_TILE_ENTITY = REGISTRY.register("secret_chest_tile_entity", () ->
            SecretRoomsServices.PLATFORM.createBlockEntityType(SecretChestTileEntity::new, SECRET_CHEST, SECRET_TRAPPED_CHEST)
    );

    public static final Supplier<BlockEntityType<SecretDaylightDetectorTileEntity>> SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY = REGISTRY.register("secret_daylight_detector_tile_entity", () ->
            SecretRoomsServices.PLATFORM.createBlockEntityType(SecretDaylightDetectorTileEntity::new, SECRET_DAYLIGHT_DETECTOR)
    );


}
