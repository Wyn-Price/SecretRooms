package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.utils.InjectedUtils;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;


@ObjectHolder(SecretRooms6.MODID)
@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SecretItems {

    public static final Item CAMOUFLAGE_PASTE = InjectedUtils.injected();

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
            new SecretBlockItem(SecretBlocks.GHOST_BLOCK, prop()).setRegistryName("ghost_block"),
            new SecretBlockItem(SecretBlocks.SECRET_STAIRS, prop()).setRegistryName("secret_stairs"),
            new SecretBlockItem(SecretBlocks.SECRET_LEVER, prop()).setRegistryName("secret_lever"),
            new SecretBlockItem(SecretBlocks.SECRET_REDSTONE, prop()).setRegistryName("secret_redstone"),
            new SecretBlockItem(SecretBlocks.ONE_WAY_GLASS, prop()).setRegistryName("one_way_glass"),
            new SecretBlockItem(SecretBlocks.SECRET_WOODEN_BUTTON, prop()).setRegistryName("secret_wooden_button"),
            new SecretBlockItem(SecretBlocks.SECRET_STONE_BUTTON, prop()).setRegistryName("secret_stone_button"),
            new WallOrFloorItem(SecretBlocks.TORCH_LEVER, SecretBlocks.WALL_TORCH_LEVER, prop()).setRegistryName("torch_lever"),
            new SecretBlockItem(SecretBlocks.SECRET_PRESSURE_PLATE, prop()).setRegistryName("secret_pressure_plate"),
            new SecretBlockItem(SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE, prop()).setRegistryName("secret_player_pressure_plate"),
            new SecretDoubleBlockItem(SecretBlocks.SECRET_DOOR, prop()).setRegistryName("secret_door"),
            new SecretDoubleBlockItem(SecretBlocks.SECRET_IRON_DOOR, prop()).setRegistryName("secret_iron_door"),
            new SecretBlockItem(SecretBlocks.SECRET_CHEST, prop()).setRegistryName("secret_chest"),
            new SecretBlockItem(SecretBlocks.SECRET_TRAPDOOR, prop()).setRegistryName("secret_trapdoor"),
            new SecretBlockItem(SecretBlocks.SECRET_IRON_TRAPDOOR, prop()).setRegistryName("secret_iron_trapdoor"),
            new SecretBlockItem(SecretBlocks.SECRET_TRAPPED_CHEST, prop()).setRegistryName("secret_trapped_chest"),
            new SecretBlockItem(SecretBlocks.SECRET_GATE, prop()).setRegistryName("secret_gate"),
            new SecretBlockItem(SecretBlocks.SECRET_TRAPPED_CHEST, prop()).setRegistryName("secret_dummy_gate"),
            new SecretBlockItem(SecretBlocks.SECRET_DAYLIGHT_DETECTOR, prop()).setRegistryName("secret_daylight_detector"),
            new SecretBlockItem(SecretBlocks.SECRET_OBSERVER, prop()).setRegistryName("secret_observer"),
            new SecretBlockItem(SecretBlocks.SECRET_CLAMBER, prop()).setRegistryName("secret_clamber"),

            new Item(prop()).setRegistryName("camouflage_paste")
        );
    }

    private static Item.Properties prop() {
        return new Item.Properties().group(SecretRooms6.TAB);
    }
}
