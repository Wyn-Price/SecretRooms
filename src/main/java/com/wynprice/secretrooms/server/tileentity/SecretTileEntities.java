package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.utils.InjectedUtils;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

@ObjectHolder(SecretRooms6.MODID)
@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SecretTileEntities {

    public static final TileEntityType<SecretTileEntity> SECRET_TILE_ENTITY = InjectedUtils.injected();
    public static final TileEntityType<SecretTileEntity> SECRET_CHEST_ENTITY = InjectedUtils.injected();
    public static final TileEntityType<SecretTileEntity> SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY = InjectedUtils.injected();

    @SubscribeEvent
    public static void onTileEntityRegister(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(SecretTileEntity::new,
                    GHOST_BLOCK, SECRET_STAIRS, SECRET_LEVER, SECRET_REDSTONE, ONE_WAY_GLASS, SECRET_WOODEN_BUTTON,
                    SECRET_STONE_BUTTON, SECRET_PRESSURE_PLATE, SECRET_PLAYER_PRESSURE_PLATE, SECRET_DOOR, SECRET_IRON_DOOR,
                    SECRET_TRAPDOOR, SECRET_IRON_TRAPDOOR, SECRET_OBSERVER, SECRET_CLAMBER
                ).build(null).setRegistryName("secret_tile_entity"),

            TileEntityType.Builder.create(SecretChestTileEntity::new, SECRET_CHEST, SECRET_TRAPPED_CHEST).build(null).setRegistryName("secret_chest_tile_entity"),

            TileEntityType.Builder.create(SecretDaylightDetectorTileEntity::new, SECRET_DAYLIGHT_DETECTOR).build(null).setRegistryName("secret_daylight_detector_tile_entity")
        );
    }

}
