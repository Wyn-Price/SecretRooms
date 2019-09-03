package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(SecretRooms6.MODID)
@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SecretItems {

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new SecretBlockItem(SecretBlocks.GHOST_BLOCK, new Item.Properties()).setRegistryName("ghost_block"),
                new SecretBlockItem(SecretBlocks.SECRET_STAIRS, new Item.Properties()).setRegistryName("secret_stairs"),
                new SecretBlockItem(SecretBlocks.SECRET_LEVER, new Item.Properties()).setRegistryName("secret_lever")

        );
    }
}
