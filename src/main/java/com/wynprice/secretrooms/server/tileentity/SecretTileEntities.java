package com.wynprice.secretrooms.server.tileentity;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.utils.InjectedUtils;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(SecretRooms6.MODID)
@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SecretTileEntities {

    public static final TileEntityType<SecretTileEntity> SECRET_TILE_ENTITY = InjectedUtils.injected();

    @SubscribeEvent
    public static void onTileEntityRegister(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(SecretTileEntity::new,
                        SecretBlocks.GHOST_BLOCK, SecretBlocks.SECRET_STAIRS, SecretBlocks.SECRET_LEVER, SecretBlocks.SECRET_REDSTONE)
                        .build(null).setRegistryName("secret_tile_entity")
        );
    }

}
