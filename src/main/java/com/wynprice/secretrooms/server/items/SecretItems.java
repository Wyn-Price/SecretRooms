package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
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
            new SecretBlockItem(SecretBlocks.SECRET_LEVER, new Item.Properties()).setRegistryName("secret_lever"),
            new SecretBlockItem(SecretBlocks.SECRET_REDSTONE, new Item.Properties()).setRegistryName("secret_redstone"),
            new SecretBlockItem(SecretBlocks.ONE_WAY_GLASS, new Item.Properties()).setRegistryName("one_way_glass"),
            new SecretBlockItem(SecretBlocks.SECRET_WOODEN_BUTTON, new Item.Properties()).setRegistryName("secret_wooden_button"),
            new SecretBlockItem(SecretBlocks.SECRET_STONE_BUTTON, new Item.Properties()).setRegistryName("secret_stone_button"),
            new WallOrFloorItem(SecretBlocks.TORCH_LEVER, SecretBlocks.WALL_TORCH_LEVER, new Item.Properties()).setRegistryName("torch_lever"),
            new SecretBlockItem(SecretBlocks.SECRET_PRESSURE_PLATE, new Item.Properties()).setRegistryName("secret_pressure_plate"),
            new SecretBlockItem(SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE, new Item.Properties()).setRegistryName("secret_player_pressure_plate"),
            new SecretDoubleBlockItem(SecretBlocks.SECRET_DOOR, new Item.Properties()).setRegistryName("secret_door"),
            new SecretDoubleBlockItem(SecretBlocks.SECRET_IRON_DOOR, new Item.Properties()).setRegistryName("secret_iron_door"),
            new SecretBlockItem(SecretBlocks.SECRET_CHEST, new Item.Properties()).setRegistryName("secret_chest")
        );
    }
}
