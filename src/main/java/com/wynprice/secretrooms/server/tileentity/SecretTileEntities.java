package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

public class SecretTileEntities {

    public static final DeferredRegister<TileEntityType<?>> REGISTRY = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, SecretRooms6.MODID);

    public static final Supplier<TileEntityType<SecretTileEntity>> SECRET_TILE_ENTITY = REGISTRY.register("secret_tile_entity", () ->
        TileEntityType.Builder.create(SecretTileEntity::new,
            GHOST_BLOCK.get(), SECRET_STAIRS.get(), SECRET_LEVER.get(), SECRET_REDSTONE.get(), ONE_WAY_GLASS.get(), SECRET_WOODEN_BUTTON.get(),
            SECRET_STONE_BUTTON.get(), SECRET_PRESSURE_PLATE.get(), SECRET_PLAYER_PRESSURE_PLATE.get(), SECRET_DOOR.get(), SECRET_IRON_DOOR.get(),
            SECRET_TRAPDOOR.get(), SECRET_IRON_TRAPDOOR.get(), SECRET_OBSERVER.get(), SECRET_CLAMBER.get()
        ).build(null)
    );

    public static final Supplier<TileEntityType<SecretChestTileEntity>> SECRET_CHEST_TILE_ENTITY = REGISTRY.register("secret_chest_tile_entity", () ->
        TileEntityType.Builder.create(SecretChestTileEntity::new, SECRET_CHEST.get(), SECRET_TRAPPED_CHEST.get()).build(null)
    );

    public static final Supplier<TileEntityType<SecretDaylightDetectorTileEntity>> SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY = REGISTRY.register("secret_daylight_detector_tile_entity", () ->
        TileEntityType.Builder.create(SecretDaylightDetectorTileEntity::new, SECRET_DAYLIGHT_DETECTOR.get()).build(null)
    );


}
